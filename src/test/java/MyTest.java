import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {
	private ArrayList<Boolean> lettersGuessed, wordGuessedInCat;
	private int currentCategory;
	
	@BeforeEach
	void setup() {
		lettersGuessed = new ArrayList();
		wordGuessedInCat = new ArrayList();
		for (int i = 0; i < 3; i++) {
			lettersGuessed.add(false);
			wordGuessedInCat.add(false);
		}
	}
	
	@Test
	void wordGuessedTest1() {
		for (int i = 0; i < 3; i++) {
			lettersGuessed.set(i, true);
		}
		assertEquals(true, wordGuessed(), "Wrong value for wordGuessed");
	}
	
	@Test
	void wordGuessedTest2() {
		assertEquals(false, wordGuessed(), "Wrong value for wordGuessed");
	}
	
	@Test
	void wordGuessedTest3() {
		currentCategory = 1;
		for (int i = 0; i < 3; i++) {
			lettersGuessed.set(i, true);
		}
		wordGuessed();
		assertEquals(true, wordGuessedInCat.get(0), "Wrong value for wordGuessed");
	}
	
	@Test
	void wordGuessedTest4() {
		currentCategory = 1;
		for (int i = 0; i < 3; i++) {
			lettersGuessed.set(i, true);
		}
		wordGuessed();
		assertEquals(false, wordGuessedInCat.get(2), "Wrong value for wordGuessed");
	}
	
	@Test
	void wordGuessedTest5() {
		currentCategory = 1;
		for (int i = 0; i < 3; i++) {
			lettersGuessed.set(i, true);
		}
		wordGuessed();
		assertEquals(false, wordGuessedInCat.get(2), "Wrong value for wordGuessed");
	}
	
	@Test
	void wordGuessedTest6() {
		currentCategory = 2;
		wordGuessed();
		assertEquals(false, wordGuessedInCat.get(2), "Wrong value for wordGuessed");
	}
	
	@Test
	void wordGuessedTest7() {
		currentCategory = 3;
		wordGuessed();
		assertEquals(false, wordGuessedInCat.get(2), "Wrong value for wordGuessed");
	}
	
	@Test
	void wordGuessedTest8() {
		currentCategory = 1;
		wordGuessed();
		assertEquals(false, wordGuessedInCat.get(1), "Wrong value for wordGuessed");
	}
	
	@Test
	void gameWinTest() {
		for (int i = 0; i < 3; i++) {
			wordGuessedInCat.set(i, true);
		}
		assertEquals(true, gameWin(), "Wrong value for gameWin");
	}
	
	@Test
	void gameWinTest2() {
		assertEquals(false, gameWin(), "Wrong value for gameWin");
	}

	
	
	public boolean wordGuessed() {
		for (int i = 0; i < lettersGuessed.size(); i++) {
			if (lettersGuessed.get(i) == false) {
				return false;
			}
		}
		if (currentCategory == 1) {
			wordGuessedInCat.set(0, true);
		} else if (currentCategory == 2) {
			wordGuessedInCat.set(1, true);
		} else if (currentCategory == 3) {
			wordGuessedInCat.set(2, true);
		}
		return true;
	}
	
	public boolean gameWin() {
		for (int i = 0; i < wordGuessedInCat.size(); i++) {
			if (wordGuessedInCat.get(i) == false) {
				return false;
			}
		}
		return true;
	}

}
