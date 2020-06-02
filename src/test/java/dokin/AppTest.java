package dokin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import lombok.SneakyThrows;
import lombok.val;

public class AppTest extends App {
	
	@SneakyThrows private void delete(Path path) { Files.delete(path); }
	
	@SneakyThrows
	@Test
	public void 適当に10ファイル程度置いてみる() {
		
		val max = 10;
		Files.list(OUT).forEach(this::delete);
		
		main();

		val names = IntStream.range(0, max).mapToObj(i->UUID.randomUUID().toString()).collect(Collectors.toList());

		for(val name : names) {
			Files.write(IN.resolve(name), name.getBytes());
		}
		
		while(Files.list(OUT).count() < max) {
			TimeUnit.SECONDS.sleep(1);
		}

		Files.list(OUT).forEach(this::delete);
	}
}
