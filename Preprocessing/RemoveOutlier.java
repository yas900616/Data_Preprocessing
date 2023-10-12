import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

public class RemoveOutlier {//麻醉 anes
	public static void main(String[] args) {
		try {
			new RemoveOutlier(args[0], args[1], args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 移除離群值
	 * @param inFile 輸入檔案
	 * @param target 移除目標欄位
	 * @param outFile 輸出檔案
	 * @throws Exception
	 */
	private RemoveOutlier(String inFile, String target, String outFile) throws Exception {
		int index = findIndex(target);		//參照欄位索引
		if(index == -1) {
			throw new Exception("找不到您輸入的欲轉換欄位編號「" + target + "」，請重新確認");
		}
		
		ArrayList<String> inputSplit;
		double number = 0.0;//數值
		String input, str;	//未切割的整行資料
		ArrayList<Double> numberAL = new ArrayList<Double>();	//數值陣列 計算四分位數
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){	//讀檔 原資料檔
			br.readLine();	//跳過標題列
			while((input = br.readLine()) != null) {	//判斷是否已到檔案結尾
				inputSplit = split(input);	//分割標題列 | 每筆資料分欄切割後的結果
				str = inputSplit.get(index);
				if(!str.isEmpty()) {
					number = Double.parseDouble(str);	//數值
					numberAL.add(number);
				}
			}
		}
		
		double[] outlier = countOutlier(numberAL);	//存放小&大離群值
		System.out.printf("| 計算結果 | 最小離群值：%f | 最大離群值：%f |\n", outlier[0], outlier[1]);
		
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){ //原資料檔
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){//寫檔 覆蓋檔案
			bw.write(br.readLine());					//第一列標題列		
			while((input = br.readLine()) != null) {	//判斷是否已到檔案結尾
				inputSplit = split(input);
				str = inputSplit.get(index);
				if(!str.isEmpty()) {	//若目標欄位非空才進行判斷
					number = Double.parseDouble(str);
					if(number < outlier[0] || number > outlier[1]) { //outlier[0](minOutlier) <= number <= outlier[1](maxOutlier) 要輸出
						continue;	//若不在範圍內不輸出
					}
				}
				bw.newLine();
				bw.write(input);
			}	
		}
		}
	}
	
	private double[] countOutlier(ArrayList<Double> numberAL){
		double size = numberAL.size();
		double Q1Index = size * 0.25;	//firstQuartileIndex
		double Q3Index = size * 0.75;	//thirdQuartileIndex
		double Q1 = 0.0;				//firstQuartile
		double Q3 = 0.0;				//thirdQuartile
		double IQR = 0.0;
		double maxOutlier = 0.0;
		double minOutlier = 0.0;
		
		Collections.sort(numberAL);	//小到大排列
		if(Double.toString(Q1Index).contains(".0")) {	//若firstQuartileIndex為整數
			Q1 = (numberAL.get((int) Q1Index - 1) + numberAL.get((int) Q1Index)) / 2;
		}else {	//若firstQuartileIndex不為整數
			Q1 = numberAL.get((int) (Math.ceil(Q1Index)) - 1);	//無條件進位
		}
		if(Double.toString(Q3Index).contains(".0")) {	//若thirdQuartileIndex為整數
			Q3 = (numberAL.get((int) Q3Index - 1) + numberAL.get((int) Q3Index)) / 2;
		}else {	//若thirdQuartileIndex不為整數
			Q3 = numberAL.get((int) (Math.ceil(Q3Index)) - 1);	//無條件進位
		}
		IQR = Q3 - Q1;
		minOutlier = Q1 - (IQR * 1.5);
		maxOutlier = Q3 + (IQR * 1.5);
		
		return new double[] {minOutlier, maxOutlier};
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