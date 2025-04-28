package alex.valker91.main;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PokerTable {
    public PokerTable(String currentStage, String currentPot, List<String> currentCommunityCards, String currentStack, List<String> myCards) {
        this.currentStage = currentStage;
        this.currentPot = currentPot;
        this.currentCommunityCards = currentCommunityCards;
        this.currentStack = currentStack;
        this.myCards = myCards;
    }

    public PokerTable() {}

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public void setCurrentStack(String currentStack) {
        this.currentStack = currentStack;
    }

    public void setCurrentPot(String currentPot) {
        this.currentPot = currentPot;
    }

    public void setCurrentCommunityCards(List<String> currentCommunityCards) {
        this.currentCommunityCards = currentCommunityCards;
    }

    private String currentStage;
    private String currentPot;

    @Override
    public String toString() {
        return "PokerTable{" +
                "currentStage='" + currentStage + '\'' +
                ", currentPot='" + currentPot + '\'' +
                ", currentCommunityCards=" + currentCommunityCards +
                ", currentStack='" + currentStack + '\'' +
                ", myCards=" + myCards +
                '}';
    }

    private List<String> currentCommunityCards;
    private String currentStack;
    private List<String> myCards;

    public String getCurrentStack() {
        return currentStack;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public String getCurrentPot() {
        return currentPot;
    }

    public List<String> getMyCards() {
        return myCards;
    }

    public void setMyCards(List<String> myCards) {
        this.myCards = myCards;
    }

    public List<String> getCurrentCommunityCards() {
        return currentCommunityCards;
    }



    // Метод для сохранения в JSON
    public void saveToFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(filePath), this);
    }

    // Метод для загрузки из JSON
    public static PokerTable readFromFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), PokerTable.class);
    }
}
