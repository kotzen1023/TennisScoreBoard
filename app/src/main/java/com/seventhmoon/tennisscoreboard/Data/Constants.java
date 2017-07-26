package com.seventhmoon.tennisscoreboard.Data;


public class Constants {
    public interface ACTION {
        String GAME_DELETE_COMPLETE = "com.seventhmoon.GameDeleteComplete";
        String GAME_SAVE_COMPLETE = "com.seventhmoon.GameSaveComplete";
        String GET_COURT_INFO_COMPLETE = "com.seventhmoon.GetCourtInfo";
        String GET_COURT_IMAGE_COMPLETE = "com.seventhmoon.GetCourtComplete";
        String INSERT_COURT_INFO_COMPLETE = "com.seventhmoon.InsertCourtInfoComplete";
        String CHECK_MAC_EXIST_COMPLETE = "com.seventhmoon.CheckMacExistComplete";
    }

    public interface DIRECTION {
        Integer SLIDE_RIGHT_DIRECTION = 0;
        Integer SLIDE_LEFT_DIRECTION = 1;
        Integer SLIDE_LAST_TO_FIRST = 2;
        Integer SLIDE_FIRST_TO_LAST = 3;
    }

    public enum STATE {
        Created,
        Idle,
        Initialized,
        Preparing,
        Prepared,
        Started,
        Paused,
        Stopped,
        PlaybackCompleted,
        End,
        Error,

    }
}
