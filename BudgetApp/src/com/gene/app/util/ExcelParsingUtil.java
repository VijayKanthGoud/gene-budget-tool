package com.gene.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelParsingUtil {

	public static List<List<Object>> readExcellData(final InputStream fileContent)
			throws InvalidFormatException, IOException {

		final List<List<Object>> tableList = new ArrayList<List<Object>>();
		try {
			final Workbook wb = WorkbookFactory.create(fileContent);
			final Sheet mySheet = wb.getSheetAt(0);
			List<Object> rowList;
			Cell cell;
			final int rowStart = mySheet.getFirstRowNum();
			final int rowEnd = mySheet.getLastRowNum();
			Row r = null;
			int lastColumn = 0;
			for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
				r = mySheet.getRow(rowNum);
				if (r != null) {
					rowList = new ArrayList();
					int a = 0;

					for (int cn = 0; cn < lastColumn; cn++) {
						cell = r.getCell(cn, r.CREATE_NULL_AS_BLANK);
						if (rowNum == 0) {
							System.out.println("cell" + cell);
							if (cell == null) {
								// throw new IllegalFormatException(
								// "Sheet contains invalid data ");
							}
						}
						switch (cell.getCellType()) {
						// Numeric Cell type (0)-----CELL_TYPE_NUMERIC
						case 0: {
							rowList.add(cell.getNumericCellValue());
							break;
						}
						// String Cell type (1)------CELL_TYPE_STRING
						case 1: {
							String wrap = cell.getStringCellValue();
							String wrap2 = wrap.replaceAll("[\r\n]", "");// to get all
																	// the lines
																	// of a cell
							rowList.add(wrap2);
							break;
						}
						// cell type formula
						case 2: {
								rowList.add(cell.getNumericCellValue());
								break;
							
						}
						// Blank Cell type (3)----CELL_TYPE_BLANK
						case 3: {
							rowList.add(null);
							a++;
						}
						}
					}
					if (a < rowList.size()) {
						tableList.add(rowList);
					}
				}
			}
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		return tableList;
	}
}
