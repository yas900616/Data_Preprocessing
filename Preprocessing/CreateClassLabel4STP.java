import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateClassLabel4STP {
	public static void main(String[] args) {
		try {
			new CreateClassLabel4STP(args[0], args[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 計算手術時間
	 * @param inFile 輸入檔案
	 * @param outFile 輸出檔案
	 * @throws Exception
	 */
	private CreateClassLabel4STP(String inFile, String outFile) throws Exception {
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){//讀檔
			ArrayList<String> inputSplit;	//每筆資料分欄切割後的結果
			StringBuilder outStr = new StringBuilder();
			int[] spendTimeSplit;	//手術時間 時與分
			int spendTime = 0;		//手術時間(分)
			
			String input = br.readLine();	//未切割的整行資料
			int spendTimeIndex = split(input).indexOf("手術時間（時:分）");	//找手術時間索引
			outStr.append(input).append(",手術時間（分）");	//補上標題
			
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){	//寫檔
			String spendTimeStr;
			while((input = br.readLine()) != null) {//判斷是否已到檔案結尾
				inputSplit = split(input);			//補回換行符並分割
				
				spendTimeStr = inputSplit.get(spendTimeIndex);
				if(spendTimeStr.contains(":")) {	//若手術時間欄包含「:」則轉換
					spendTimeSplit = Arrays.stream(spendTimeStr.split(":")).mapToInt(Integer::parseInt).toArray();
					//以:為分隔符號 將手術時間的時與分 分開
					spendTime = spendTimeSplit[0] * 60 + spendTimeSplit[1];
				}else spendTime = 0;	//否則直接輸出0
				
				outStr.append('\n').append(input).append(",").append(spendTime);
				bw.write(outStr.toString());
				outStr.setLength(0);
			}	
		}
		}
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
}