package io.github.gegame;

import java.util.LinkedHashSet;
import java.util.Set;

public class World {
    protected Set<Entity> entities = new LinkedHashSet<>();
 
    public Set<Entity> getEntities() {
        return entities;
    }

}
