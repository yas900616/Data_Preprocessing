import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class NormalizeFeature {
	public static void main(String[] args) {
		try {
			new NormalizeFeature(args[0], args[1], args[2], Arrays.copyOfRange(args, 3, args.length));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 正規化
	 * @param inTrain 輸入訓練集
	 * @param target 目標欄位
	 * @param outTrain 輸出訓練集 
	 * @param test 測試集陣列(可選)
	 * @throws Exception
	 */
	private NormalizeFeature(String inTrain, String target, String outTrain, String... test) throws Exception {
		int index = findIndex(target);	//欲轉換欄位轉換為索引值
		if(index == -1) {	// 若找不到欲轉換欄位
			throw new Exception("在輸入檔找不到您輸入的欲轉換欄位編號「" + target + "」，請重新確認");
		}
		
		ArrayList<String> inputSplit;	//每筆資料分欄切割後的結果
		String input, str;	//未切割的整行資料
		double max = Double.MIN_VALUE, min = Double.MAX_VALUE, value;
		try(BufferedReader br = new BufferedReader(new FileReader(inTrain))){	//讀資料檔
			input = br.readLine();		//讀取標題列
			inputSplit = split(input);	//分割標題列 | 每筆資料分欄切割後的結果
			while((input = br.readLine()) != null) {//判斷是否已到檔案結尾
				inputSplit = split(input);
				str = inputSplit.get(index);
				if(!str.isEmpty()) {
					value = Double.parseDouble(str);
					max = (max < value) ? value : max;
					min = (min > value) ? value : min;	
				}
			}	
		}
		double deviation = max - min;	//最大最小值的差值
		if(deviation == 0.0) {
			throw new Exception(target + "欄位的差值為0，無法產生結果。");
		}
		
		// 訓練集 | 測試集 輸出入檔名
		Queue<String> file = new LinkedList<>();
		file.offer(inTrain);
		file.offer(outTrain);
		for(String s: test) {	//加入輸入測試集
			file.offer(s);
		}
		
		StringBuilder outStr = new StringBuilder();
		BigDecimal bd;
		Double normNumber;
		while(!file.isEmpty()) {
			try(BufferedReader br = new BufferedReader(new FileReader(file.poll()))){ 	//原資料檔
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(file.poll()))){	//檔案 覆蓋檔案
				bw.write(br.readLine());					//第一列標題列
				while((input = br.readLine()) != null) {	//判斷是否已到檔案結尾
					inputSplit = split(input);
					for(int j = 0, size = inputSplit.size(); j < size; j++) {
						str = inputSplit.get(j);
						if(j == index && !str.isEmpty()) {	//是欲轉換欄位且非空植才輸出數值
							value = Double.parseDouble(str);
							normNumber = (value - min) / deviation;
							bd = new BigDecimal(normNumber.toString());
							outStr.append(bd.toPlainString());	//輸出化數值
						}else {		//否則直接輸出
							outStr.append(str);
						}
						if(j != size - 1) {
							outStr.append(",");
						}
					}
					bw.newLine();
					bw.write(outStr.toString());
					outStr.setLength(0);	
				}	
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
	
	private int findIndex(String transform) {
		int index = -1;//參照欄位索引(每行索引值從0開始)			
		for(int i = 0, length = transform.length(); i < length; i++) {
			index += Math.pow(26, length - i - 1) * (transform.charAt(i) - 64);
			//26進制的轉換
		}		
		return index;
	}
}