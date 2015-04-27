package hedspi.k55.sm.ultil;

import hedspi.k55.sm.model.Portfolio;
import hedspi.k55.sm.model.Stock;
import hedspi.k55.sm.model.Transaction;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import au.com.bytecode.opencsv.CSVWriter;

public class FileManager {
	public void exportCSV(Context context, String fileName) {
		Portfolio portfolio = new Portfolio(context, "DEFAULT_PORTFOLIO");
		portfolio.init();
		List<Stock> list = new ArrayList<Stock>();
		list = portfolio.getAll();

		CSVWriter writer = null;
		File exportDir = new File(Environment.getExternalStorageDirectory(), "");
		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}

		File file = new File(exportDir, fileName + ".csv");
		try {
			file.createNewFile();
			writer = new CSVWriter(new FileWriter(file));
			String arrCol[] = { "Name", "Symbol", "Last price", "Change",
					"Shares", "Cost basis", "Mkt value", "Gain", "Gain %",
					"Day's gain", "Overall Return" };
			writer.writeNext(arrCol);
			for (int i = 0; i < list.size(); i++) {
				Double daysGain = Double.parseDouble(list.get(i).getChange())
						* Double.parseDouble(list.get(i).getMyShares());
				String arrData[] = { list.get(i).getCompanyName(),
						list.get(i).getSymbol(), list.get(i).getLastPrice(),
						list.get(i).getChange(), list.get(i).getMyShares(),
						String.valueOf(list.get(i).getCostBasic()),
						String.valueOf(list.get(i).getMktValue()),
						String.valueOf(list.get(i).getGain()),
						String.valueOf(list.get(i).getGainPerc()),
						String.valueOf(daysGain),
						String.valueOf(list.get(i).getGainPerc()) };
				writer.writeNext(arrData);
			}
			String arrCurrentMoney[] = { "Cash", "",
					String.valueOf(portfolio.getCurrentMoney()), "", "", "",
					String.valueOf(portfolio.getCurrentMoney()) };
			writer.writeNext(arrCurrentMoney);
			writer.close();
		} catch (Exception sqlEx) {
			sqlEx.printStackTrace();
		}

	}

	public void exportStock(Context context, String fileName, List<Stock> list) {
		CSVWriter writer = null;
		File exportDir = new File(Environment.getExternalStorageDirectory(), "");
		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}
		File file = new File(exportDir, fileName + ".csv");
		try {
			file.createNewFile();
			writer = new CSVWriter(new FileWriter(file));
			String arrCol[] = { "Name", "Symbol", "Last price", "Change",
					"Shares", "Cost basis", "Mkt value", "Gain", "Gain %",
					"Day's gain", "Overall Return" };
			writer.writeNext(arrCol);
			for (int i = 0; i < list.size(); i++) {
				Double daysGain = Double.parseDouble(list.get(i).getChange())
						* Double.parseDouble(list.get(i).getMyShares());
				String arrData[] = { list.get(i).getCompanyName(),
						list.get(i).getSymbol(), list.get(i).getLastPrice(),
						list.get(i).getChange(), list.get(i).getMyShares(),
						String.valueOf(list.get(i).getCostBasic()),
						String.valueOf(list.get(i).getMktValue()),
						String.valueOf(list.get(i).getGain()),
						String.valueOf(list.get(i).getGainPerc()),
						String.valueOf(daysGain),
						String.valueOf(list.get(i).getGainPerc()) };
				writer.writeNext(arrData);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exportTransaction(Context context, String fileName) {
		Portfolio portfolio = new Portfolio(context, "DEFAULT_PORTFOLIO");
		portfolio.init();
		List<Transaction> list = new ArrayList<Transaction>();
		list = portfolio.getAllTransaction();

		CSVWriter writer = null;
		File exportDir = new File(Environment.getExternalStorageDirectory(), "");
		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}
		File file = new File(exportDir, fileName + ".csv");
		try {
			file.createNewFile();
			writer = new CSVWriter(new FileWriter(file));
			String arrCol[] = { "Symbol", "Name", "Type", "Date", "Shares",
					"Price", "Cash value", "Commission", "Notes" };
			writer.writeNext(arrCol);
			for (int i = 0; i < list.size(); i++) {
				Stock stock = new Stock(context, list.get(i).getStockSymbol());
				stock.init();
				if (list.get(i).getType() > 0) {
					String arrData[] = { list.get(i).getStockSymbol(),
							stock.getCompanyName(), list.get(i).getTypeStr(),
							list.get(i).getTimeFormal(),
							String.valueOf(list.get(i).getShares()),
							String.valueOf(list.get(i).getPrice()), "",
							String.valueOf(list.get(i).getCommission()), "" };
					writer.writeNext(arrData);
				} else {
					String arrData[] = { "", "Cash", list.get(i).getTypeStr(),
							list.get(i).getTimeFormal(), "", "",
							String.valueOf(list.get(i).getPrice()), "", "" };
					writer.writeNext(arrData);
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
