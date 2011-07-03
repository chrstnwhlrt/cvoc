package com.googlemail.christian667.cvoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public abstract class HamsterToolkit {

	public static final String CHPVERSION = "cHamsterProtocolV1.0";
	public static final String CHARSET = "UTF-8";

	// Android differing

	public static void debug(String tag, String msg) {
		System.out.println(tag + ":\t\t" + msg);
	}

	public static byte[] base64Decode(String BASE64String) {
		try {
			return new BASE64Decoder().decodeBuffer(BASE64String);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String base64(String inString) {
		return new BASE64Encoder().encode(getBytes(inString));
	}

	public static String base64(byte[] inBytes) {
		return new BASE64Encoder().encode(inBytes);
	}

	// Android differing end

	public static byte[] getBytes(String convertString) {
		try {
			return convertString.getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getMD5Sum(byte[] bytes) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return digest.digest(bytes);
	}

	public static byte[] aXorB(byte[] aByte, byte[] bByte) {
		// For size correction
		byte[] cByte = new byte[aByte.length];

		// Setting correct size of b
		if (aByte.length > bByte.length) {
			for (int i = 0; i < cByte.length; i++) {
				if (i < bByte.length)
					cByte[i] = bByte[i];
				else
					cByte[i] = bByte[i % bByte.length];
			}
		} else if (aByte.length < bByte.length)
			for (int i = 0; i < aByte.length; i++)
				cByte[i] = bByte[i];
		else
			cByte = bByte;

		byte[] resultByte = new byte[aByte.length];
		for (int i = 0; i < aByte.length; i++) {
			resultByte[i] = (byte) (aByte[i] ^ cByte[i]);
		}
		return resultByte;
	}

	public static int getCurrentUnixTime() {
		return (int) (System.currentTimeMillis() / 1000L);
	}

	public static int getPositivRandomIntIn(int intervalBegin, int intervalEnd) {
		int random = abs(new Random(System.currentTimeMillis()).nextInt());
		if (random < intervalBegin)
			random = intervalEnd - random;
		else
			while (random > intervalEnd)
				random = abs(intervalEnd - ((random - intervalEnd) / 2));
		return random;
	}

	public static int abs(int integer) {
		if (integer < 0)
			return -integer;
		else
			return integer;
	}

	public static short unsignedShortByteArrayToShort(byte[] b) {
		short i = 0;
		i |= b[0] & 0xFF;
		i |= ((b[1] & 0xFFFF) << 8);
		return i;
	}

	public static byte[] unsignedShortToShortByteArray(short s) {
		byte[] b = new byte[2];
		b[0] = (byte) (s & 0xFF);
		b[1] = (byte) ((s >> 8) & 0xFF);
		return b;
	}

	public static int unsignedIntByteArrayToInt(byte[] b) {
		int i = 0;
		i |= b[0] & 0xFF;
		i |= ((b[1] & 0xFFFF) << 8);
		i |= ((b[2] & 0xFFFFFF) << 16);
		i |= ((b[3] & 0xFFFFFFFF) << 24);
		return i;
	}

	public static byte[] unsignedIntToIntByteArray(int s) {
		byte[] b = new byte[4];
		b[0] = (byte) (s & 0xFF);
		b[1] = (byte) ((s >> 8) & 0xFF);
		b[2] = (byte) ((s >> 16) & 0xFF);
		b[3] = (byte) ((s >> 24) & 0xFF);
		return b;
	}

	public static String getCurrentTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(cal.getTime());
	}

	private static void sendStringLine(String message,
			BufferedWriter bufferedOutData) throws IOException {
		bufferedOutData.write(message + "\n");
		bufferedOutData.flush();
	}

	private static String readNextStringLine(BufferedReader bufferedInData)
			throws IOException {
		String tmpString = "";
		try {
			while (tmpString.length() < 1)
				tmpString = bufferedInData.readLine();
			return tmpString.replace("\n", "");

		} catch (NullPointerException e) {
			return "RVJST1IK";
		}
	}

	public static boolean serverAuthentication(Socket socket, String debug,
			HashMap<String, String> logins) throws IOException {

		// Check debug verbosity
		boolean verbose = false;
		if (debug != null)
			verbose = true;

		if (verbose)
			debug(debug, "Starting authentication");

		// Create BufferedReader
		BufferedReader bufferedInData = new BufferedReader(
				new InputStreamReader(socket.getInputStream(), CHARSET));

		// Read clients 'hello'
		// hello:='HamsterSec1 + B64(NONCE:UNIXTIME)'
		String hello = readNextStringLine(bufferedInData);
		if (verbose)
			debug(debug, "Should be Client-'hello':" + hello);

		// Check protocol correctness
		if (!hello.contains(CHPVERSION)) {
			if (verbose)
				debug(debug, "Close connection, wrong protocoll.");
			return false;
		}

		// Client should be O.K., protocol correct, lets go on

		// It's e
		final byte[] hamsterSec1 = { 2, 7, 1, 8, 2, 8, 1, 8, 3 };
		// And pi ;)
		final byte[] hamsterSec2 = { 3, 1, 4, 1, 5, 9, 2, 6, 5 };

		// BufferedWriter
		BufferedWriter bufferedOutData = new BufferedWriter(
				new OutputStreamWriter(socket.getOutputStream(), CHARSET));

		// Decrypt rnonse from clients 'hello'
		String usernamernonce64 = hello.split(CHPVERSION)[1];
		String usernameStringXorRnonce64 = usernamernonce64.split(":")[0];
		byte[] rnonce = base64Decode(usernamernonce64.split(":")[1]);

		final byte[] username = aXorB(base64Decode(usernameStringXorRnonce64),
				rnonce);
		String usernameString = new String(username, CHARSET);

		// Return false if the username not in map
		if (!logins.containsKey(usernameString))
			return false;

		// Get the username - and password
		final byte[] password = getBytes(logins.get(usernameString));

		if (verbose)
			debug(debug, "RNONCE:" + usernamernonce64.split(":")[1]);

		// Prepare challenge for client
		// First get cnonce
		byte[] cnonce = getFromInt(getPositivRandomIntIn(50, 10000));

		if (verbose)
			debug(debug, "CNONCE:" + base64(cnonce) + " Length:"
					+ cnonce.length);
		// Calculate challenge
		// challenge:='B64[HamsterSec2 XOR (RNONCE XOR CNONCE)]'
		byte[] challenge = aXorB(aXorB(cnonce, rnonce), hamsterSec2);
		String challengeB64 = base64(challenge);
		// Send challenge (+\n)
		sendStringLine(challengeB64, bufferedOutData);
		if (verbose)
			debug(debug, "Challenge send:" + base64(challenge) + " Length:"
					+ challenge.length);

		// Prepare response for auth check
		// Calculate it first
		// response:='B64[MD5[HamsterSec1 XOR (USERNAME XOR MD5(CONCE XOR
		// PASSWORD)]]'
		byte[] passwordXORcnonce = aXorB(password, cnonce);
		if (verbose)

			debug(debug, "B64 passwordXORcnonce:" + base64(passwordXORcnonce)
					+ " Length:" + passwordXORcnonce.length);

		byte[] passwordXORcnonceMD5 = getMD5Sum(passwordXORcnonce);
		if (verbose)
			debug(debug, "B64 passwordXORcnonceMD5:"
					+ base64(passwordXORcnonceMD5) + " Length:"
					+ passwordXORcnonceMD5.length);

		byte[] passwordXORcnonceMD5XORusername = aXorB(passwordXORcnonceMD5,
				username);
		if (verbose)
			debug(debug, "B64 passwordXORcnonceMD5XORusername:"
					+ base64(passwordXORcnonceMD5XORusername) + " Length:"
					+ passwordXORcnonceMD5XORusername.length);
		byte[] passwordXORcnonceMD5XORusernameXORhamsterSec1 = aXorB(
				passwordXORcnonceMD5XORusername, hamsterSec1);
		if (verbose)

			debug(debug, "B64 passwordXORcnonceMD5XORusernameXORhamsterSec1:"
					+ base64(passwordXORcnonceMD5XORusernameXORhamsterSec1)
					+ " Length:"
					+ passwordXORcnonceMD5XORusernameXORhamsterSec1.length);
		// The whole response
		String response = base64(getMD5Sum(passwordXORcnonceMD5XORusernameXORhamsterSec1));
		if (verbose)
			debug(debug, "Waiting for response:" + response);

		// Read response from client
		String clientResponse = readNextStringLine(bufferedInData);
		if (verbose)
			debug(debug, "Got response:" + clientResponse);

		// Check for correctness, return false if wrong
		if (!clientResponse.contains(response)) {
			if (verbose)
				debug(debug, "Authentication failed");
			sendStringLine("FAILED", bufferedOutData);
			return false;
		}

		// Responses equal, send 'OK' package
		if (verbose)
			debug(debug, "Authentication correct");
		sendStringLine("SUCCESS", bufferedOutData);
		System.out.println("'" + usernameString + "' logged in");
		return true;
	}

	public static boolean clientAuthentication(Socket socket, String debug,
			String usernameString, String passwordString) throws IOException {
		// It's e
		final byte[] hamsterSec1 = { 2, 7, 1, 8, 2, 8, 1, 8, 3 };
		// And pi ;)
		final byte[] hamsterSec2 = { 3, 1, 4, 1, 5, 9, 2, 6, 5 };
		final byte[] username = getBytes(usernameString);
		final byte[] password = getBytes(passwordString);

		boolean verbose = false;
		if (debug != null)
			verbose = true;

		BufferedWriter bufferedOutData = new BufferedWriter(
				new OutputStreamWriter(socket.getOutputStream(), CHARSET));
		BufferedReader bufferedInData = new BufferedReader(
				new InputStreamReader(socket.getInputStream(), CHARSET));

		if (verbose)
			debug(debug, "Starting authentication");

		// send hello
		String rnonce = getPositivRandomIntIn(10, 10000) + ":"
				+ getCurrentUnixTime();
		if (verbose)
			debug(debug, "RNONCE:" + rnonce);

		String rnonceB64 = base64(rnonce);
		String hello = CHPVERSION
				+ base64(aXorB(username, base64Decode(rnonceB64))) + ":"
				+ rnonceB64;

		if (verbose)
			debug(debug, "Sending Client-'hello':" + hello);
		sendStringLine(hello, bufferedOutData);

		// get challenge
		String challengeB64 = readNextStringLine(bufferedInData);
		byte[] challenge = base64Decode(challengeB64);

		if (verbose)
			debug(debug, "Challenge received:" + challengeB64 + " Length:"
					+ challenge.length);

		// Calculate CNONCE
		byte[] cnonce = aXorB(aXorB(challenge, hamsterSec2),
				base64Decode(rnonceB64));
		if (verbose)
			debug(debug, "CNONCE calculated, should be:" + base64(cnonce));

		// Prepare response for auth check
		// Calculate it first
		// response:='B64[MD5[HamsterSec1 XOR (USERNAME XOR MD5(CONCE XOR
		// PASSWORD)]]'
		byte[] passwordXORcnonce = aXorB(password, cnonce);
		if (verbose)

			debug(debug, "B64 passwordXORcnonce:" + base64(passwordXORcnonce)
					+ " Length:" + passwordXORcnonce.length);

		byte[] passwordXORcnonceMD5 = getMD5Sum(passwordXORcnonce);
		if (verbose)
			debug(debug, "B64 passwordXORcnonceMD5:"
					+ base64(passwordXORcnonceMD5) + " Length:"
					+ passwordXORcnonceMD5.length);

		byte[] passwordXORcnonceMD5XORusername = aXorB(passwordXORcnonceMD5,
				username);
		if (verbose)
			debug(debug, "B64 passwordXORcnonceMD5XORusername:"
					+ base64(passwordXORcnonceMD5XORusername) + " Length:"
					+ passwordXORcnonceMD5XORusername.length);
		byte[] passwordXORcnonceMD5XORusernameXORhamsterSec1 = aXorB(
				passwordXORcnonceMD5XORusername, hamsterSec1);
		if (verbose)
			debug(debug, "B64 passwordXORcnonceMD5XORusernameXORhamsterSec1:"
					+ base64(passwordXORcnonceMD5XORusernameXORhamsterSec1)
					+ " Length:"
					+ passwordXORcnonceMD5XORusernameXORhamsterSec1.length);
		// The whole response
		String response = base64(getMD5Sum(passwordXORcnonceMD5XORusernameXORhamsterSec1));
		if (verbose)
			debug(debug, "Response calculated, sending:" + response);

		sendStringLine(response, bufferedOutData);
		// get OK
		String result = "";
		try {
			result = readNextStringLine(bufferedInData);
		} catch (Exception e) {
			return false;
		}
		if (result.contains("SUCCESS"))
			return true;
		else
			return false;
	}

	public static byte[] getFromInt(int number) {
		byte[] data = new byte[4];
		for (int i = 0; i < 4; ++i) {
			int shift = i << 3; // i * 8
			data[3 - i] = (byte) ((number & (0xff << shift)) >>> shift);
		}
		return data;
	}
}