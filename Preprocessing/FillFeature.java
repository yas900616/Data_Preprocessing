import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class FillFeature {
	public static void main(String[] args) {
		try {
			new FillFeature(args[0], args[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 填補缺失值
	 * @param in 輸入路徑
	 * @param out 輸出路徑
	 * @throws Exception
	 */
	private FillFeature(String inFile, String outFile) throws Exception {
		String input;			//未切割的整行資料
		String[] inputSplit;	//每筆資料分欄切割後的結果
		String date = "";		//日期
		String room = "";		//室別
		StringBuilder outStr = new StringBuilder();
		
		try(BufferedReader br = new BufferedReader(new FileReader(inFile))) { 	//原資料檔
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {	//檔案 覆蓋檔案
			bw.write(br.readLine());			//第一列標題列
			while((input = br.readLine()) != null) {	//判斷是否已到檔案結尾
				inputSplit = input.split(",", 3);		//將每列切成三份
				
				if(!inputSplit[0].isEmpty()) {	//如果該欄有日期
					date = inputSplit[0];		//將該欄日期放入date
				}
				if(!inputSplit[1].isEmpty()) {	//如果該欄有室別
					room = inputSplit[1];		//將該欄室別放入room
				}
				outStr.append(date).append(",").append(room).append(",").append(inputSplit[2]);
				bw.newLine();
				bw.write(outStr.toString());			
				outStr.setLength(0);
			}	
		}
		}
	}
}