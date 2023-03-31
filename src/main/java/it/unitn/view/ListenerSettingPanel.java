package it.unitn.view;


import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Setter
@Getter
public class ListenerSettingPanel {
    private JTextField cryptoNameField;
    private JTextField listenerSearchDirField;
    private JTextField listenerFileNamePatternField;
    private JCheckBox recurseListenerSubDirs;
}
