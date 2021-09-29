package com.jd.dart.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.jd.dart.ui.DartPageGeneratorDialog;

public class JDCreateDartPageAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null ) {
            return;
        }
        String psiPath = e.getData(PlatformDataKeys.PSI_ELEMENT).toString();
        psiPath = psiPath.substring(psiPath.indexOf(":") + 1);
        DartPageGeneratorDialog dialog = new DartPageGeneratorDialog(psiPath);
        dialog.pack();
        dialog.setVisible(true);
        project.getProjectFile().refresh(false,true);
    }

}
