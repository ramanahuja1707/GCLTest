package com.gcltest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;

public class GCLReadTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private StorageService ss = new StorageService();
	String line = "";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		OutputStream out = resp.getOutputStream();

		resp.setContentType("image/jpg");
		FileService fileService = FileServiceFactory.getFileService();
		AppEngineFile readableFile = new AppEngineFile("/gs/bucket30sept/"
				+ req.getParameter("cloudFile"));
		FileReadChannel readChannel = fileService.openReadChannel(readableFile,
				false);

		ByteBuffer buff = ByteBuffer.allocate(10 * 1024 * 1024);

		int len = 0;

		while ((len = readChannel.read(buff)) > 0) {
			out.write(buff.array(), 0, len);
		}

		// Again, different standard Java ways of reading from the channel.
		//
		// BufferedReader reader = new BufferedReader(Channels.newReader(
		// readChannel, "UTF8"));

		// while (reader.readLine() != null) {
		// line += reader.readLine();
		// }

		// line = "The woods are lovely, dark, and deep."
		// resp.getWriter().println("");
		//
		readChannel.close();
		out.flush();

	}
}
