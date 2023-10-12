import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;

public class TargetEncoding {
	public static void main(String[] args) {
		try {
			new TargetEncoding(args[0], args[1], args[2], args[3], Arrays.copyOfRange(args, 4, args.length));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 數值化
	 * @param inTrain 輸入訓練集
	 * @param target 目標欄位
	 * @param refer 參照欄位
	 * @param outTrain 輸出訓練集 
	 * @param test 測試集陣列(可選)
	 * @throws Exception
	 */
	private TargetEncoding(String inTrain, String target, String refer, String outTrain, String... test) throws Exception {
		//refer = reference | numer = numeralization
		int index = findIndex(target);	//將轉換欄位轉換為索引值
		if(index == -1) {
			throw new Exception("找不到您輸入的欲轉換欄位編號「" + target + "」，請重新確認");
		}
		int referIndex = findIndex(refer);	//參照欄位的索引
		if(referIndex == -1) {
			throw new Exception("找不到您輸入的參照欄位編號「" + refer + "」，請重新確認");
		}
		
		HashMap<String, String> numerHM;
		ArrayList<String> inputSplit;
		String input, referStr, str;	//未切割的整行資料 | 參照文字 | 取出文字
		try(BufferedReader br = new BufferedReader(new FileReader(inTrain))) {//讀檔 原資料檔
			br.readLine();//跳過標題列
			
			HashMap<String, ArrayList<Double>> referNumHM = new HashMap<String, ArrayList<Double>>();
			//目標轉換欄位<名稱, 對應數值陣列(雙精度數值)>
			double referNum = 0.0;	//參照欄位的數字
			while((input = br.readLine()) != null) {//判斷是否已到檔案結尾
				inputSplit = split(input);	//分割標題列 | 每筆資料分欄切割後的結果
				referStr = inputSplit.get(referIndex);
				if(!referStr.isEmpty()) {	//如果參照欄位不為空... 否則忽略整列資料
					referNum = Double.parseDouble(referStr);	//轉換
					str = inputSplit.get(index);
					if(!str.isEmpty()) {	//若欲轉換欄位為空則忽略
						referNumHM.putIfAbsent(str, new ArrayList<Double>());	//比對目標欄位名稱 若沒有該名稱 加入新的陣列
						referNumHM.get(str).add(referNum);		//將參照欄位數字加入
					}
				}
			}
			numerHM = numeralize(referNumHM);	//將originalHashMap數值化後的HashMap
		}
		
		// 訓練集 | 測試集 輸出入檔名
		Queue<String> file = new LinkedList<>();
		file.offer(inTrain);
		file.offer(outTrain);
		for(String s: test) {	//加入輸入測試集
			file.offer(s);
		}
		
		// 訓練集 | 測試集 輸出檔名
		StringBuilder outStr = new StringBuilder();
		while(!file.isEmpty()) {
			try(BufferedReader br = new BufferedReader(new FileReader(file.poll()))) { 	//原資料檔
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(file.poll()))) {	//檔案 覆蓋檔案
				bw.write(br.readLine());					//第一列標題列
				while((input = br.readLine()) != null) {	//判斷是否已到檔案結尾
					inputSplit = split(input);
					
					for(int i = 0, size = inputSplit.size(); i < size; i++) {
						str = inputSplit.get(i);
						if(i == index) {		//如果是欲轉換欄位
							if(str.isEmpty()) {	//若為空格
								outStr.append("0.0");	//輸出0
							}else {	//若非空格
								outStr.append(numerHM.getOrDefault(str, "0.0"));	//輸出數值化數值
							}
						}else {	//如果不是欲轉換欄位
							outStr.append(str);	//直接輸出
						}
						if(i != size - 1) {
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
	
	private HashMap<String, String> numeralize(HashMap<String, ArrayList<Double>> referNumHM) { //陣列所存數字 平均/頻率
		double average = 0.0;	//平均值
		Double numerNumber;
		BigDecimal bd;
		HashMap<String, String> numerHM = new HashMap<String, String>(referNumHM.size());
		//將originalHashMap數值化後的HashMap
		
		for(Entry<String, ArrayList<Double>> e: referNumHM.entrySet()) {
			average = e.getValue().stream().mapToDouble(Double::doubleValue).average().getAsDouble();
			numerNumber = average / e.getValue().size();//平均值/陣列大小
			bd = new BigDecimal(numerNumber.toString());
			numerHM.put(e.getKey(), bd.toPlainString());
		}
		return numerHM;
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