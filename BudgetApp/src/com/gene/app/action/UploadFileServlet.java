package com.gene.app.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.gene.app.util.ExcelParsingUtil;

@SuppressWarnings("serial")
public class UploadFileServlet extends HttpServlet {
	private final static Logger LOGGER = Logger
			.getLogger(UploadFileServlet.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "inside fileupload...");
		final FileItemFactory fileItemFactory = new DiskFileItemFactory();
		final ServletFileUpload servletFileUpload = new ServletFileUpload(
				fileItemFactory);
		try {
			final FileItemIterator fileItemIterator = servletFileUpload
					.getItemIterator(req);
			// FileItemStream fileItemStream = null;
			while (fileItemIterator.hasNext()) {
				final FileItemStream fileItemStream = (FileItemStream) fileItemIterator
						.next();
				String uploadedFileName;
				if (fileItemStream.isFormField()) {
					uploadedFileName = fileItemStream.getName();
				} else if (fileItemStream.getFieldName().equals("file")) {
					uploadedFileName = fileItemStream.getName();
					LOGGER.log(Level.INFO, "UPLOAD FILE NAME :"
							+ uploadedFileName);
					InputStream inputStream = fileItemStream.openStream();
					InputStream stream = new ByteArrayInputStream(
							IOUtils.toByteArray(inputStream));
					ExcelParsingUtil.readExcellData(inputStream);
				}
			}
		} catch (IOException | FileUploadException | InvalidFormatException exception) {
			LOGGER.log(Level.SEVERE,
					"ERROR OCCURED DURING  FILE UPLOAD. eRROR DETAILS : "
							+ exception);
		}
	}

}
