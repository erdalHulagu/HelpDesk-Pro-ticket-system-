
package com.erdal.helpdeskpro.service;

import java.io.IOException;
import java.util.List;

public interface ExcelExportService {

	byte[] exportToExcel(String sheetName, List<String> headers, List<List<String>> data) throws IOException;

   
}
