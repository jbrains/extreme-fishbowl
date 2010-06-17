/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.vfs.provider;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;

/**
 * Utilities for dealing with URIs. See RFC 2396 for details.
 * 
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision: 480428 $ $Date: 2005-10-13 21:11:33 +0200 (Do, 13 Okt
 *          2005) $
 */
public final class UriParser
{
	/**
	 * The normalised separator to use.
	 */
	private static final char SEPARATOR_CHAR = FileName.SEPARATOR_CHAR;

	/**
	 * The set of valid separators. These are all converted to the normalised
	 * one. Does <i>not</i> contain the normalised separator
	 */
	// public static final char[] separators = {'\\'};
	public static final char TRANS_SEPARATOR = '\\';

	private UriParser()
	{
	}

	/**
	 * Extracts the first element of a path.
	 */
	public static String extractFirstElement(final StringBuffer name)
	{
		final int len = name.length();
		if (len < 1)
		{
			return null;
		}
		int startPos = 0;
		if (name.charAt(0) == SEPARATOR_CHAR)
		{
			startPos = 1;
		}
		for (int pos = startPos; pos < len; pos++)
		{
			if (name.charAt(pos) == SEPARATOR_CHAR)
			{
				// Found a separator
				final String elem = name.substring(startPos, pos);
				name.delete(startPos, pos + 1);
				return elem;
			}
		}

		// No separator
		final String elem = name.substring(startPos);
		name.setLength(0);
		return elem;
	}

	/**
	 * Normalises a path. Does the following:
	 * <ul>
	 * <li>Removes empty path elements.
	 * <li>Handles '.' and '..' elements.
	 * <li>Removes trailing separator.
	 * </ul>
	 * 
	 * Its assumed that the separators are already fixed.
	 * 
	 *  @see #fixSeparators
	 */
	public static FileType normalisePath(final StringBuffer path)
			throws FileSystemException
	{
		FileType fileType = FileType.FOLDER;
		if (path.length() == 0)
		{
			return fileType;
		}

		if (path.charAt(path.length() - 1) != '/')
		{
			fileType = FileType.FILE;
		}

		// Adjust separators
		// fixSeparators(path);

		// Determine the start of the first element
		int startFirstElem = 0;
		if (path.charAt(0) == SEPARATOR_CHAR)
		{
			if (path.length() == 1)
			{
				return fileType;
			}
			startFirstElem = 1;
		}

		// Iterate over each element
		int startElem = startFirstElem;
		int maxlen = path.length();
		while (startElem < maxlen)
		{
			// Find the end of the element
			int endElem = startElem;
			for (; endElem < maxlen && path.charAt(endElem) != SEPARATOR_CHAR; endElem++)
			{
			}

			final int elemLen = endElem - startElem;
			if (elemLen == 0)
			{
				// An empty element - axe it
				path.delete(endElem, endElem + 1);
				maxlen = path.length();
				continue;
			}
			if (elemLen == 1 && path.charAt(startElem) == '.')
			{
				// A '.' element - axe it
				path.delete(startElem, endElem + 1);
				maxlen = path.length();
				continue;
			}
			if (elemLen == 2 && path.charAt(startElem) == '.'
					&& path.charAt(startElem + 1) == '.')
			{
				// A '..' element - remove the previous element
				if (startElem == startFirstElem)
				{
					// Previous element is missing
					throw new FileSystemException(
							"vfs.provider/invalid-relative-path.error");
				}

				// Find start of previous element
				int pos = startElem - 2;
				for (; pos >= 0 && path.charAt(pos) != SEPARATOR_CHAR; pos--)
				{
				}
				startElem = pos + 1;

				path.delete(startElem, endElem + 1);
				maxlen = path.length();
				continue;
			}

			// A regular element
			startElem = endElem + 1;
		}

		// Remove trailing separator
		if (!VFS.isUriStyle())
		{
			if (maxlen > 0 && path.charAt(maxlen - 1) == SEPARATOR_CHAR
					&& maxlen > 1)
			{
				path.delete(maxlen - 1, maxlen);
			}
		}

		return fileType;
	}

	/**
	 * Normalises the separators in a name.
	 */
	public static boolean fixSeparators(final StringBuffer name)
	{
		boolean changed = false;
		final int maxlen = name.length();
		for (int i = 0; i < maxlen; i++)
		{
			final char ch = name.charAt(i);
			if (ch == TRANS_SEPARATOR)
			{
				name.setCharAt(i, SEPARATOR_CHAR);
				changed = true;
			}
		}
		return changed;
	}

	/**
	 * Extracts the scheme from a URI.
	 * 
	 * @param uri
	 *            The URI.
	 * @return The scheme name. Returns null if there is no scheme.
	 */
	public static String extractScheme(final String uri)
	{
		return extractScheme(uri, null);
	}

	/**
	 * Extracts the scheme from a URI. Removes the scheme and ':' delimiter from
	 * the front of the URI.
	 * 
	 * @param uri
	 *            The URI.
	 * @param buffer
	 *            Returns the remainder of the URI.
	 * @return The scheme name. Returns null if there is no scheme.
	 */
	public static String extractScheme(final String uri,
			final StringBuffer buffer)
	{
		if (buffer != null)
		{
			buffer.setLength(0);
			buffer.append(uri);
		}

		final int maxPos = uri.length();
		for (int pos = 0; pos < maxPos; pos++)
		{
			final char ch = uri.charAt(pos);

			if (ch == ':')
			{
				// Found the end of the scheme
				final String scheme = uri.substring(0, pos);
				if (buffer != null)
				{
					buffer.delete(0, pos + 1);
				}
				return scheme.intern();
			}

			if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
			{
				// A scheme character
				continue;
			}
			if (pos > 0
					&& ((ch >= '0' && ch <= '9') || ch == '+' || ch == '-' || ch == '.'))
			{
				// A scheme character (these are not allowed as the first
				// character of the scheme, but can be used as subsequent
				// characters.
				continue;
			}

			// Not a scheme character
			break;
		}

		// No scheme in URI
		return null;
	}

	/**
	 * Removes %nn encodings from a string.
	 */
	public static String decode(final String encodedStr)
			throws FileSystemException
	{
		if (encodedStr == null)
		{
			return null;
		}
		if (encodedStr.indexOf('%') < 0)
		{
			return encodedStr;
		}
		final StringBuffer buffer = new StringBuffer(encodedStr);
		decode(buffer, 0, buffer.length());
		return buffer.toString();
	}

	/**
	 * Removes %nn encodings from a string.
	 */
	public static void decode(final StringBuffer buffer, final int offset,
			final int length) throws FileSystemException
	{
		int index = offset;
		int count = length;
		for (; count > 0; count--, index++)
		{
			final char ch = buffer.charAt(index);
			if (ch != '%')
			{
				continue;
			}
			if (count < 3)
			{
				throw new FileSystemException(
						"vfs.provider/invalid-escape-sequence.error", buffer
								.substring(index, index + count));
			}

			// Decode
			int dig1 = Character.digit(buffer.charAt(index + 1), 16);
			int dig2 = Character.digit(buffer.charAt(index + 2), 16);
			if (dig1 == -1 || dig2 == -1)
			{
				throw new FileSystemException(
						"vfs.provider/invalid-escape-sequence.error", buffer
								.substring(index, index + 3));
			}
			char value = (char) (dig1 << 4 | dig2);

			// Replace
			buffer.setCharAt(index, value);
			buffer.delete(index + 1, index + 3);
			count -= 2;
		}
	}

	/**
	 * Encodes and appends a string to a StringBuffer.
	 */
	public static void appendEncoded(final StringBuffer buffer,
			final String unencodedValue, final char[] reserved)
	{
		final int offset = buffer.length();
		buffer.append(unencodedValue);
		encode(buffer, offset, unencodedValue.length(), reserved);
	}

	/**
	 * Encodes a set of reserved characters in a StringBuffer, using the URI %nn
	 * encoding. Always encodes % characters.
	 */
	public static void encode(final StringBuffer buffer, final int offset,
			final int length, final char[] reserved)
	{
		int index = offset;
		int count = length;
		for (; count > 0; index++, count--)
		{
			final char ch = buffer.charAt(index);
			boolean match = (ch == '%');
			if (reserved != null)
			{
				for (int i = 0; !match && i < reserved.length; i++)
				{
					if (ch == reserved[i])
					{
						match = true;
					}
				}
			}
			if (match)
			{
				// Encode
				char[] digits =
				{ Character.forDigit(((ch >> 4) & 0xF), 16),
						Character.forDigit((ch & 0xF), 16) };
				buffer.setCharAt(index, '%');
				buffer.insert(index + 1, digits);
				index += 2;
			}
		}
	}

	/**
	 * Removes %nn encodings from a string.
	 */
	public static String encode(final String decodedStr)
	{
		return encode(decodedStr, null);
	}

	public static String encode(final String decodedStr, final char[] reserved)
	{
		if (decodedStr == null)
		{
			return null;
		}
		final StringBuffer buffer = new StringBuffer(decodedStr);
		encode(buffer, 0, buffer.length(), reserved);
		return buffer.toString();
	}

	public static String[] encode(String[] strings)
	{
		if (strings == null)
		{
			return null;
		}
		for (int i = 0; i < strings.length; i++)
		{
			strings[i] = encode(strings[i]);
		}
		return strings;
	}

	public static void checkUriEncoding(String uri) throws FileSystemException
	{
		decode(uri);
	}

	public static void canonicalizePath(StringBuffer buffer, int offset,
			int length, FileNameParser fileNameParser)
			throws FileSystemException
	{
		int index = offset;
		int count = length;
		for (; count > 0; count--, index++)
		{
			final char ch = buffer.charAt(index);
			if (ch == '%')
			{
				if (count < 3)
				{
					throw new FileSystemException(
							"vfs.provider/invalid-escape-sequence.error",
							buffer.substring(index, index + count));
				}

				// Decode
				int dig1 = Character.digit(buffer.charAt(index + 1), 16);
				int dig2 = Character.digit(buffer.charAt(index + 2), 16);
				if (dig1 == -1 || dig2 == -1)
				{
					throw new FileSystemException(
							"vfs.provider/invalid-escape-sequence.error",
							buffer.substring(index, index + 3));
				}
				char value = (char) (dig1 << 4 | dig2);

				boolean match = (value == '%')
						|| (fileNameParser != null && fileNameParser
								.encodeCharacter(value));

				if (match)
				{
					// this is a reserved character, not allowed to decode
					index += 2;
					count -= 2;
					continue;
				}

				// Replace
				buffer.setCharAt(index, value);
				buffer.delete(index + 1, index + 3);
				count -= 2;
			}
			else if (fileNameParser.encodeCharacter(ch))
			{
				// Encode
				char[] digits =
				{ Character.forDigit(((ch >> 4) & 0xF), 16),
						Character.forDigit((ch & 0xF), 16) };
				buffer.setCharAt(index, '%');
				buffer.insert(index + 1, digits);
				index += 2;
			}
		}
	}

	public static String extractQueryString(StringBuffer name)
	{
		for (int pos = 0; pos < name.length(); pos++)
		{
			if (name.charAt(pos) == '?')
			{
				String queryString = name.substring(pos + 1);
				name.delete(pos, name.length());
				return queryString;
			}
		}

		return null;
	}
}
