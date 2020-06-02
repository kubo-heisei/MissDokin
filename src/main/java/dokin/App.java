package dokin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Random;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
	
	protected static final Path IN = Paths.get("./hash/IN");
	protected static final Path OUT = Paths.get("./hash/OUT");
	
	/**
	 * ./hash/{IN,OUT}フォルダを作成する
	 */
	@SneakyThrows
	protected static void init() {
		Files.createDirectories(IN);
		Files.createDirectories(OUT);
	}
	
	private static final Random rand = new Random(System.currentTimeMillis());
	
	@SneakyThrows
	public static void calcError(Path src) {
		log.warn(" - ERROR " + src.getFileName());
		Files.write(OUT.resolve(src.getFileName() + "_err.xml"), "ERROR".getBytes());
	}
	
	@SneakyThrows
	public static void calcHash(Path src) {
		log.info("calc hash - " + src);
		
		try {
			// 1/10でエラーにする
			if(rand.nextInt(10) == 0) {
				calcError(src);
				return;
			}
			
			val md = MessageDigest.getInstance("SHA-256");
			
			md.update(Files.readAllBytes(src));
	
			val sb = new StringBuilder();
	        for(byte b: md.digest()) {
	        	sb.append(String.format("%02x", b&0xff));
	        }
	
			Files.write(OUT.resolve(src.getFileName() + ".xml"), sb.toString().getBytes());
			log.info(" - SUCCESS " + src.getFileName());
		}
		finally {
			Files.delete(src);
		}
	}
	
	@SneakyThrows
    public static void main(String...args) {
		log.info("※Ctrl + Cで終了");
		init();
    	
    	Executors.newSingleThreadExecutor().submit(()-> {
        	new Monitor(IN, App::calcHash).start();
    	}).get();
    }
}
