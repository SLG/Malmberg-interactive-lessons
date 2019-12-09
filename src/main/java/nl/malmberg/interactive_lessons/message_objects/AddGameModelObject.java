package nl.malmberg.interactive_lessons.message_objects;

import nl.malmberg.interactive_lessons.model.GameModel;

public class AddGameModelObject {
    private final String id;
    private final GameModel gameModel;

    public AddGameModelObject(final String id, final GameModel gameModel) {
        this.id = id;
        this.gameModel = gameModel;
    }

    public String getId() {
        return id;
    }

    public GameModel getGameModel() {
        return gameModel;
    }
}
