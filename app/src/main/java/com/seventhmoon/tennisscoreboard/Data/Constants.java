package com.seventhmoon.tennisscoreboard.Data;


public class Constants {
    public interface ACTION {
        String GAME_DELETE_COMPLETE = "com.seventhmoon.GameDeleteComplete";
        String GAME_SAVE_COMPLETE = "com.seventhmoon.GameSaveComplete";
        String GET_COURT_INFO_COMPLETE = "com.seventhmoon.GetCourtInfo";
        String GET_COURT_IMAGE_COMPLETE = "com.seventhmoon.GetCourtComplete";
        String INSERT_COURT_INFO_COMPLETE = "com.seventhmoon.InsertCourtInfoComplete";
        String CHECK_MAC_EXIST_COMPLETE = "com.seventhmoon.CheckMacExistComplete";

        String GET_SEARCHLIST_ACTION = "com.seventhmoon.GetSearchListAction";
        String ADD_FILE_LIST_COMPLETE = "com.seventhmoon.AddFileListComplete";

        String SAVE_CURRENT_STATE_ACTION = "com.seventhmoon.SaveCurrentStateAction";
        String SAVE_CURRENT_STATE_COMPLETE = "com.seventhmoon.SaveCurrentStateComplete";

        String PLAY_MULTIFILES_COMPLETE = "com.seventhmoon.PlayMultiFilesComplete";

        String IMPORT_FILE_ACTION = "com.seventhmoon.ImportFileAction";
        String IMPORT_FILE_COMPLETE = "com.seventhmoon.ImportFileComplete";
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

    public enum VOICE_TYPE {
        GBR_MAN,
        GBR_WOMAN,
        USER_RECORD,
    }
}
