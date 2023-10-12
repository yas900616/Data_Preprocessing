import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class RemoveFeature {
	public static void main(String[] args) {
		int last = args.length - 1;
		try {
			new RemoveFeature(args[0], Arrays.copyOfRange(args, 1, last), args[last]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 移除欄位
	 * @param inFile 輸入檔案
	 * @param target 移除欄標題陣列
	 * @param outFile 輸出檔案
	 * @throws Exception
	 */
	private RemoveFeature(String inFile, String[] target, String outFile) throws Exception {
		int length = target.length;
		int[] index = new int[length];
		for(int i = 0; i < length; i++) {
			index[i] = findIndex(target[i]);//將欲移除欄位依序轉換為索引值
			if(index[i] == -1) {	// 若找不到欲轉換欄位
				throw new Exception("找不到您輸入的欲轉換欄位編號「" + target[i] + "」，請重新確認");
			}
		}
		Arrays.sort(index);	// 升排序
		
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))){ 	//原資料檔
			String input = br.readLine();	//第一列標題列 | 未切割的整行資料
			ArrayList<String> inputSplit = split(input);	//分割標題列 | 每筆資料分欄切割後的結果
			
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))){	//輸出檔案 覆蓋檔案
			bw.write(outputLine(inputSplit, index));	//輸出標題
			while((input = br.readLine()) != null) {	//判斷是否已到檔案結尾
				inputSplit = split(input);
				bw.newLine();
				bw.write(outputLine(inputSplit, index));
			}
		}	
		}
	}
	
	private String outputLine(ArrayList<String> inputSplit, int[] index) {
		StringBuilder tempString = new StringBuilder();
		int inputSize = inputSplit.size();
		int dynamic = inputSize - 1;	//預設為沒有移除最後的欄位 | 每行倒數第一個不加","
		int indexLength = index.length;
		if(index[indexLength -1] == inputSize - 1) {	//若使用者欲移除最後的欄位
			dynamic -= 1;	//更改調整值 | 每行倒數第二個不加","
		}
		int n = 0;
		for(int i = 0; i < inputSize; i++) {
			if(indexLength > n && i == index[n]) {
				n += 1;
			}else {		//如果為非指定移除欄位 輸出
				tempString.append(inputSplit.get(i));
				if(i != dynamic) {
					tempString.append(",");
				}
			}
		}
		return tempString.toString();
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
