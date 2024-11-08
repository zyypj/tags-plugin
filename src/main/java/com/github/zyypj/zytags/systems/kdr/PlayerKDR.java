package com.github.zyypj.zytags.systems.kdr;

import lombok.Getter;

@Getter
public class PlayerKDR {

    private int kills;
    private int deaths;

    public void addKill() {
        kills++;
    }

    public void addDeath() {
        deaths++;
    }

    public void addKills(int kills) {
        this.kills += kills;
    }

    public void addDeaths(int deaths) {
        this.deaths += deaths;
    }
}
