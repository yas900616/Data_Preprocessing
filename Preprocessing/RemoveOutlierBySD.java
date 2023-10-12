import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class RemoveOutlierBySD {
	public static void main(String[] args) {
		try {
			new RemoveOutlierBySD(args[0], args[1], args[2], args[3]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param inFile 資料輸入檔名
	 * @param target 移除欄位索引
	 * @param multiple 標準差倍數
	 * @param outFile 資料輸出檔名
	 * @throws Exception
	 */
	private RemoveOutlierBySD(String inFile, String target, String multiple, String outFile) throws Exception {
		int index = findIndex(target);	//參照欄位索引
		if(index == -1) {
			throw new Exception("找不到您輸入的欲轉換欄位編號「" + target + "」，請重新確認");
		}
		
		ArrayList<Double> numAL = new ArrayList<Double>(); // 目標欄位數值 陣列
		ArrayList<String> inputSplit;
		String input, str;	// 未切割的整行資料 | 單個欄位值
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){ // 讀檔
			br.readLine();	//跳過標題列
			while ((input = br.readLine()) != null) { // 判斷是否已到檔案結尾
				inputSplit = split(input);	// 分割標題列 | 每筆資料分欄切割後的結果
				str = inputSplit.get(index);
				if (!str.isEmpty()) {
					numAL.add(Double.valueOf(str));
				}
			}
		}
		double[] outlier = countOutlier(numAL, Double.parseDouble(multiple));
		System.out.printf("| 計算結果 | 最小離群值：%f | 最大離群值：%f |\n", outlier[0], outlier[1]);
		
		double number;
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){	// 原資料檔
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){	// 檔案 覆蓋檔案
			bw.write(br.readLine());// 第一列標題列
			while ((input = br.readLine()) != null) { // 判斷是否已到檔案結尾
				inputSplit = split(input);
				str = inputSplit.get(index);
				if(!str.isEmpty()) {
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
	
	/**
	 * 計算離群值
	 * @param numAL 數值陣列
	 * @param multiple 幾倍的標準差
	 * @return 下界離群值 | 上界離群值
	 */
	private double[] countOutlier(ArrayList<Double> numAL, double multiple) {
		double average = numAL.stream().mapToDouble(Double::doubleValue).average().getAsDouble();	//平均值
		double sum = numAL.stream().mapToDouble(eachNumber -> Math.pow((eachNumber - average), 2)).sum();
		double sd = Math.sqrt(sum / numAL.size());	//標準差
		double adjustedSD = multiple * sd;
		return new double[] {average - adjustedSD, average + adjustedSD};
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