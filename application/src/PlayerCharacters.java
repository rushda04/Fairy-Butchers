public class PlayerCharacters {
    public String name;
    public String png;
    public int atk;
    public int hp;
    public int baseMana;
    public int currentMana;


    public PlayerCharacters(){

    }

    public PlayerCharacters(String name, String png, int atk, int hp, int baseMana) {
        this.name = name;
        this.png = png;
        this.atk = atk;
        this.hp = hp;
        this.baseMana = baseMana;
        this.currentMana = baseMana;
    }
}