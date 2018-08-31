package vip.ourcraft.programs.bossshopexcelinitiator;

public class MinecraftItem {
    private String chineseName;
    private String englishName;
    private int id;
    private int durability;

    public MinecraftItem(String chineseName, String englishName, int id, int durability) {
        this.chineseName = chineseName;
        this.englishName = englishName;
        this.id = id;
        this.durability = durability;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }
}
