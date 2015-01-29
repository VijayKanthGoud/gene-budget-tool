package com.gene.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelParsingUtil {
	private final static Logger LOGGER = Logger
			.getLogger(ExcelParsingUtil.class.getName());
	public final static String NO_DATA = "NO DATA";
	public static List<List<String>> readExcellData(
			final InputStream fileContent, String sheetName, final int rowStart, final int lastColumn)
			throws InvalidFormatException, IOException {
		LOGGER.log(Level.INFO, "inside ExcelParsingUtil...");
		List<List<String>> rowList = new ArrayList<List<String>>();
		List<String> rowData = new ArrayList<String>();
		try {

			final Workbook wb = WorkbookFactory.create(fileContent);
			final Sheet mySheet;
			if (sheetName.trim().length() == 0) {
				mySheet = wb.getSheetAt(0);
			} else {
				mySheet = wb.getSheet(sheetName);
			}
			Cell cell;
			
			final int rowEnd = mySheet.getLastRowNum();
			Row r = null;
			for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
				boolean nonEmptyRow = true;
				r = mySheet.getRow(rowNum);
				rowData.clear();
				if (r != null) {
					for (int cn = 0; cn < lastColumn; cn++) {
						cell = r.getCell(cn, r.CREATE_NULL_AS_BLANK);
						System.out.println("cell" + cell + ":::::::::" + rowNum);
						
						switch (cell.getCellType()) {
						// Numeric Cell type (0)-----CELL_TYPE_NUMERIC
						case 0: {
							rowData.add(((Double) cell.getNumericCellValue())
									.toString());
							nonEmptyRow = false;
							break;
						}
						// String Cell type (1)------CELL_TYPE_STRING
						case 1: {
							String wrap = cell.getStringCellValue();
							String wrap2 = wrap.replaceAll("[\r\n]", "");
							rowData.add(wrap2);
							nonEmptyRow = false;
							break;
						}
						// cell type formula
						case 2: {
							rowData.add((((Double) cell.getNumericCellValue())
									.toString()));
							nonEmptyRow = false;
							break;

						}
						// Blank Cell type (3)----CELL_TYPE_BLANK
						case 3: {
							rowData.add(NO_DATA);
						}
						}
						
					}
				}
				rowList.add(new ArrayList(rowData));
				if (nonEmptyRow) {
					break;
				}
			}
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		LOGGER.log(Level.INFO, "List created : " + rowList.size());
		return rowList;
	}
}
