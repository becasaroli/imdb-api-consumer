package imdbapiconsumer;


import java.io.IOException;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImdbApiConsumerApplication {

	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://imdb-api.com/en/API/Top250Movies/k_drw70l8b"))
				.GET()
                .build();

        		HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
				
				String json = response.body();
				String[] moviesArray = parseJsonMovies(json);

				List<String> titles = parseTitles(moviesArray);
				titles.forEach(System.out::println);

				List<String> urls = parseUrlImages(moviesArray);
				urls.forEach(System.out::println);

	
	}
	private static List<String> parseTitles(String[] moviesArray) {
		return parseAttribute(moviesArray, 5);
	}

	private static List<String> parseUrlImages(String[] moviesArray) {
		return parseAttribute(moviesArray, 3);
	}

	private static String[] parseJsonMovies(String json) {
		Matcher matcher = Pattern.compile(".*\\[(.*)\\].*").matcher(json);

		if (!matcher.matches()) {
			throw new IllegalArgumentException("no match in " + json);
		}

		String[] moviesArray = matcher.group(1).split("\\},\\{");
		moviesArray[0] = moviesArray[0].substring(1);
		int last = moviesArray.length - 1;
		String lastString = moviesArray[last];
		moviesArray[last] = lastString.substring(0, lastString.length() - 1);
		return moviesArray;
	}

	private static List<String> parseAttribute(String[] moviesArray, int pos) {
		return Stream.of(moviesArray)
			.map(e -> e.split("\",\"")[pos]) 
			.map(e -> e.split(":\"")[1]) 
			.map(e -> e.replaceAll("\"", ""))
			.collect(Collectors.toList());
	}
	

}
