import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AppendRecord {
	public static void main(String[] args) {
		try {
			new AppendRecord(args[0], args[1], args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 執行AppendRecord
	 * @param inFile 輸入檔案
	 * @param outFile 輸出檔案
	 * @throws Exception
	 */
	private AppendRecord(String inFile1, String inFile2, String outFile) throws Exception {
		Files.copy(Paths.get(inFile1), Paths.get(outFile), StandardCopyOption.REPLACE_EXISTING);
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFile, true))){
			try(BufferedReader br = new BufferedReader(new FileReader(inFile2))) {
				br.readLine();//跳過標題列
				String input;
				while((input = br.readLine()) != null) {
					bw.newLine();
					bw.write(input);
				}
			}
		}
	}
}