import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class ExtractRecord {
	public static void main(String[] args) {
		try {
			new ExtractRecord(args[0], args[1], args[2], args[3]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 提取紀錄
	 * @param inFile 輸入檔案
	 * @param target 目標欄位
	 * @param content 目標值
	 * @param outFile 輸出檔案
	 * @throws Exception
	 */
	private ExtractRecord(String inFile, String target, String content, String outFile) throws Exception {
		int index = findIndex(target);	//目標欄位索引(每行索引值從0開始)
		if(index == -1) {
			throw new Exception("找不到您輸入的欲轉換欄位編號「" + target + "」，請重新確認");
		}
		
		String specify = content;	//欲比較數字
		int mode = -1;				//比對模式
		if(content.equalsIgnoreCase("NOTNULL")) {		//若關鍵字為非空值
			mode = 0;
		}else if(content.equalsIgnoreCase("NULL") || content.isEmpty()) {	//若關鍵字為空值
			mode = 1;
		}else if(content.startsWith(">=")) {
			specify = content.substring(2);
			mode = 2;
		}else if(content.startsWith("<=")) {
			specify = content.substring(2);
			mode = 3;
		}else if(content.charAt(0) == '=') {
			specify = content.substring(1);
			mode = 4;
		}else if(content.charAt(0) == '>'){
			specify = content.substring(1);
			mode = 5;
		}else if(content.charAt(0) == '<'){
			specify = content.substring(1);
			mode = 6;
		}
		
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){ //原資料檔
			String input = br.readLine();	//標題 | 未切割的整行資料
			ArrayList<String> inputSplit = split(input);	//標題分割 | 每筆資料分欄切割後的結果
			
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile, false))){	//檔案 覆蓋檔案
			bw.write(input);	//寫入第一列標題列
			while((input = br.readLine()) != null) {	//判斷是否已到檔案結尾
				inputSplit = split(input);
				if(compare(mode, inputSplit.get(index), specify)) {
					bw.newLine();
					bw.write(input);
				}
			}	
		}
		}
	}
	
	private boolean compare(int mode, String content, String specify) {	//搜尋模式 | 檔案指定欄位內容 | 指定字串
		switch (mode) {
		case 0: // NOTNULL
			if (!content.isEmpty()) {
				return true;
			}
			break;
		case 1: // NULL
			if (content.isEmpty()) {
				return true;
			}
			break;
		case 2: // >=
			if (!content.isEmpty() && (Double.parseDouble(content) >= Double.parseDouble(specify))) {
				return true;
			}
			break;
		case 3: // <=
			if (!content.isEmpty() && (Double.parseDouble(content) <= Double.parseDouble(specify))) {
				return true;
			}
			break;
		case 4: // =
			if (!content.isEmpty() && (Double.parseDouble(content) == Double.parseDouble(specify))) {
				return true;
			}
			break;
		case 5: // >
			if (!content.isEmpty() && (Double.parseDouble(content) > Double.parseDouble(specify))) {
				return true;
			}
			break;
		case 6: // <
			if (!content.isEmpty() && (Double.parseDouble(content) < Double.parseDouble(specify))) {
				return true;
			}
			break;
		default:
			if (content.equals(specify)) {
				return true;
			}
			break;
		}
		return false;
	}
	
	private ArrayList<String> split(String input) {
		boolean inQuotes = false;			//判斷某逗號是否在雙引號中
		StringBuilder tempString = new StringBuilder();	//存放逐字元判斷所串接的字串
		ArrayList<String> inputSplit = new ArrayList<String>();	//每筆資料分欄切割後的結果
		for (char c: input.toCharArray()) {	//將input轉換為字元陣列並依序讀出
			if(c == ',' && !inQuotes) {
				inputSplit.add(tempString.toString());
			    tempString.setLength(0);	//將tempString清空
			}else if(c == '\"') {			//雙引號
				inQuotes = !inQuotes;
				tempString.append('\"');	//補回"
			}else{
				tempString.append(c);		//其他字元加到tempString中
			}
		}
		inputSplit.add(tempString.toString());
		return inputSplit;
	}
	
	private int findIndex(String transform) {
		int index = -1;//參照欄位索引(每行索引值從0開始)			
		for(int i = 0, length = transform.length(); i < length; i++) {
			index += Math.pow(26, length - i - 1) * (transform.charAt(i) - 64);
			//26進制的轉換
		}		
		return index;
	}
}