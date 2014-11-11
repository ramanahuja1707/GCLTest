package com.gcltest;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.logging.Logger;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

public class StorageService {
	public static final String BUCKETNAME = "bucket30sept";
	public static final String FILENAME = "check.txt";
	private static final int BUFFER_SIZE = 1024 * 1024;
	String fileName;
	String mime;
	byte[] b;
	private OutputStream os = null;
	GcsOutputChannel writeChannel;
	private static final Logger log = Logger.getLogger(StorageService.class
			.getName());

	public void init(String fileName, String mime) throws Exception {
		System.out.println("Storage service:init() method:  file name:"
				+ fileName + " and mime:" + mime);
		log.info("Storage service:init() method:  file name:" + fileName
				+ " and mime:" + mime);
		GcsService gcsService = GcsServiceFactory.createGcsService();
		GcsFilename filename = new GcsFilename(BUCKETNAME, "15263/" + fileName);
		GcsFileOptions options = new GcsFileOptions.Builder().mimeType(mime)
				.acl("public-read")
				.addUserMetadata("myfield1", "my field value").build();

		writeChannel = gcsService.createOrReplace(filename, options);
		writeChannel.waitForOutstandingWrites();
		os = Channels.newOutputStream(writeChannel);
		// You can write to the channel using the standard Java methods.
		// Here we use a PrintWriter:
		/*
		 * PrintWriter writer = new PrintWriter(Channels.newWriter(writeChannel,
		 * "UTF8")); writer.println("The woods are lovely dark and deep.");
		 * writer.println("But I have promises to keep."); writer.flush();
		 */

		// Note that the writeChannel is Serializable, so it is possible to
		// store it somewhere and write
		// more to the file in a separate request. To make the object as small
		// as possible call:

		// This time we write to the channel directly
		// writeChannel.write(ByteBuffer.wrap("And miles to go before I sleep."
		// .getBytes("UTF8")));
		// writeChannel.write(ByteBuffer.wrap(b));

		// If you want partial content saved in case of an exception, close the
		// GcsOutputChannel in a finally block. See the GcsOutputChannel
		// interface
		// javadoc for more information.

		// At this point, the file is visible to anybody on the Internet through
		// Cloud Storage as:
		// (http://storage.googleapis.com/BUCKETNAME/FILENAME)

		/*
		 * GcsInputChannel readChannel = null; BufferedReader reader = null; try
		 * { // We can now read the file through the API: readChannel =
		 * gcsService.openReadChannel(filename, 0); // Again, different standard
		 * Java ways of reading from the channel. reader = new
		 * BufferedReader(Channels.newReader(readChannel, "UTF8")); String line;
		 * // Prints "The woods are lovely, dark, and deep." //
		 * "But I have promises to keep." // "And miles to go before I sleep."
		 * while ((line = reader.readLine()) != null) {
		 * resp.getWriter().println("READ:" + line); } } finally { if (reader !=
		 * null) { reader.close(); } }
		 */
		// os = Channels.newOutputStream(writeChannel);
	}

	public void storeFile(byte[] b, int readSize) throws Exception {
		os.write(b, 0, readSize);
		os.flush();
	}

	public void destroy() throws Exception {
		log.info("Storage service: destroy() method");
		os.close();
		writeChannel.close();
	}

	public String readTextFileOnly(String fileName) throws Exception {
		log.info("Reading the txt file from google cloud storage...........");
		// String filename = "/gs/" + BUCKETNAME + "/" + fileName;
		FileService fileService = FileServiceFactory.getFileService();
		AppEngineFile readableFile = new AppEngineFile("/gs/printphotobucket/"
				+ fileName);
		FileReadChannel readChannel = fileService.openReadChannel(readableFile,
				false);
		// Again, different standard Java ways of reading from the channel.
		BufferedReader reader = new BufferedReader(Channels.newReader(
				readChannel, "UTF8"));
		String line = reader.readLine();
		// line = "The woods are lovely, dark, and deep."
		readChannel.close();
		return line;

	}
}