public class Fairy {
    public int hp;
    public int airAb;
    public int groundAb;
    public int maxHp;

    double scaling = 1.0;

    public Fairy(int ground, int aerial, int hp) {
        this.groundAb = ground;
        this.airAb = aerial;
        this.hp = hp;
        this.maxHp = hp;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public int getGroundDmg(){
        return (int)(groundAb * scaling);
    }

    public int getAirDmg(){
        return (int)(airAb * scaling);
    }

    public void increaseScaling(){
        scaling += Math.random();
    }


}
