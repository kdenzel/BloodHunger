package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;

public class EnemyComponent implements Component {

    public float speed = MathUtils.random(50f,200f);
    public float health = MathUtils.random(50f,200f);

}
