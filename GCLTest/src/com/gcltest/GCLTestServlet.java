package com.gcltest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@SuppressWarnings("serial")
public class GCLTestServlet extends HttpServlet {
	private OutputStream os = null;
	private StorageService storage = new StorageService();
	public static final String BUCKETNAME = "printphotobucket";
	public static final String FILENAME = "check.txt";
	private static final int BUFFER_SIZE = 1024 * 1024;
	private static final Logger log = Logger.getLogger(GCLTestServlet.class
			.getName());
	String fileName;
	String mime;
	byte[] b;
	int fileCount = 1;

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PrintWriter out = resp.getWriter();
		log.info(this.getServletInfo() + " Servlets called....");
		resp.setContentType("text/html");
		resp.getWriter()
				.println(
						"Now see here your file content, that you have uploaded on storage..");

		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world from java");

		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter;
		try {
			iter = upload.getItemIterator(req);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				fileName = item.getName();
				mime = item.getContentType();

				storage.init(fileName, mime);
				InputStream is = item.openStream();

				b = new byte[BUFFER_SIZE];
				int readBytes = is.read(b, 0, BUFFER_SIZE);
				while (readBytes != -1) {
					storage.storeFile(b, readBytes);
					/*
					 * os.write(b, 0, readBytes); os.flush();
					 */
					readBytes = is.read(b, 0, readBytes);
				}

				// http: //
				// login.smsgatewayhub.com/smsapi/pushsms.aspx?user=ramanahuja188@gmail.com&pwd=769943
				// &to=919560804766&sid=hello&msg=test%20message&fl=0&gwid=2

				is.close();
				storage.destroy();

				resp.getWriter().println(fileCount + ": File uploading done");
				fileCount++;
				// resp.getWriter().println("READ:" +
				// storage.readTextFileOnly(fileName));

				log.info(this.getServletName() + " ended....");

			}
		} catch (FileUploadException e) {
			out.println("FileUploadException::" + e.getMessage());
			System.out.println("FileUploadException::" + e.getMessage());
			log.severe(this.getServletName() + ":FileUploadException::"
					+ e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			out.println(this.getServletName() + ":Exception::" + e.getMessage()
					+ e.getCause() + "  " + e.getStackTrace());
			log.severe(this.getServletName() + ":Exception::" + e.getMessage());
			System.out.println("Exception::" + e.getMessage());
			e.printStackTrace();
		}

		/*
		 * GcsService gcsService = GcsServiceFactory.createGcsService();
		 * GcsFilename filename = new GcsFilename(BUCKETNAME, fileName);
		 * GcsFileOptions options = new GcsFileOptions.Builder().mimeType(mime)
		 * .acl("public-read") .addUserMetadata("myfield1",
		 * "my field value").build();
		 * 
		 * GcsOutputChannel writeChannel = gcsService.createOrReplace(filename,
		 * options);
		 * 
		 * os = Channels.newOutputStream(writeChannel); // You can write to the
		 * channel using the standard Java methods. // Here we use a
		 * PrintWriter: /* PrintWriter writer = new
		 * PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
		 * writer.println("The woods are lovely dark and deep.");
		 * writer.println("But I have promises to keep."); writer.flush();
		 */

		// Note that the writeChannel is Serializable, so it is possible to
		// store it somewhere and write
		// more to the file in a separate request. To make the object as small
		// as possible call:
		// writeChannel.waitForOutstandingWrites();

		// This time we write to the channel directly
		// writeChannel.write(ByteBuffer.wrap("And miles to go before I sleep."
		// .getBytes("UTF8")));
		// writeChannel.write(ByteBuffer.wrap(b));
		// writeChannel.write(ByteBuffer.wrap(b));

		// If you want partial content saved in case of an exception, close the
		// GcsOutputChannel in a finally block. See the GcsOutputChannel
		// interface
		// javadoc for more information.
		/*
		 * writeChannel.close(); resp.getWriter().println("Done writing...");
		 */

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
		/*
		 * resp.setContentType("text/plain");
		 * resp.getWriter().println("Hello, world");
		 */
	}
}
