package edu.illinois.phantom.ui;

import edu.illinois.phantom.model.*;
import edu.illinois.phantom.analysisengine.*;

import edu.illinois.phantom.searchengine.SearchEngine;
import javafx.event.*;
import java.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;

public class ResumeFinderController {
  @FXML
  private TextField CorpusDirTextfield;

  @FXML
  private TextField SkillTextfield1;

  @FXML
  private TextField SkillTextfield2;

  @FXML
  private TextField SkillTextfield3;

  @FXML
  private Spinner<Integer> ExpSpinner1;

  @FXML
  private Spinner<Integer> ExpSpinner2;

  @FXML
  private Spinner<Integer> ExpSpinner3;

  @FXML
  private CheckBox MandatoryCheckbox1;

  @FXML
  private CheckBox MandatoryCheckbox2;

  @FXML
  private CheckBox MandatoryCheckbox3;

  @FXML
  private ListView ResumeLocationList;

  @FXML
  private Button FindResumes;

  @FXML
  private void initialize() {
    final int initialValue = 1;

    // Value factories for the experience spinners
    SpinnerValueFactory<Integer> valueFactory1 = //
      new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, initialValue);

    SpinnerValueFactory<Integer> valueFactory2 = //
      new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, initialValue);

    SpinnerValueFactory<Integer> valueFactory3 = //
      new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, initialValue);

    ExpSpinner1.setValueFactory(valueFactory1);

    ExpSpinner2.setValueFactory(valueFactory2);

    ExpSpinner3.setValueFactory(valueFactory3);

    // Button to find resumes
    FindResumes.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        //Creating a dialog
        //Dialog<String> dialog = new Dialog<String>();
        //dialog.setTitle("Dialog");
        //dialog.showAndWait();

        // Get skills
        String skill1 = SkillTextfield1.getText().trim().replaceAll("\\s{2,}", " ");
        String skill2 = SkillTextfield2.getText().trim().replaceAll("\\s{2,}", " ");
        String skill3 = SkillTextfield3.getText().trim().replaceAll("\\s{2,}", " ");

        // Get min experience
        Integer exp1 = ExpSpinner1.getValue() * 12;
        Integer exp2 = ExpSpinner2.getValue() * 12;
        Integer exp3 = ExpSpinner3.getValue() * 12;

        // Get mandatory flags
        boolean mustHaveSkill1 = MandatoryCheckbox1.isSelected();
        boolean mustHaveSkill2 = MandatoryCheckbox2.isSelected();
        boolean mustHaveSkill3 = MandatoryCheckbox3.isSelected();

        // Add to an list if skill is given
        ArrayList<UserQuery> skills = new ArrayList();

        if (skill1 != null && skill1.length() > 0) {
          skills.add(new UserQuery(skill1, exp1, mustHaveSkill1));
        }

        if (skill2 != null && skill2.length() > 0) {
          skills.add(new UserQuery(skill2, exp2, mustHaveSkill2));
        }

        if (skill3 != null && skill3.length() > 0) {
          skills.add(new UserQuery(skill3, exp3, mustHaveSkill3));
        }

        try {
          // Analysis Engine
          SearchEngine searchEngine = new SearchEngine(CorpusDirTextfield.getText().trim());

          // Perform the search
          Set<String> locations = searchEngine.searchQuery(skills);
          // Populate results in list view
          ResumeLocationList.getItems().clear();
          for (String location : locations) {

            //take only the file name and remove the folder path
            int i = location.lastIndexOf("/");
            if (i==-1){  i = location.lastIndexOf("\\"); }

            i = i+1;

            location = location.substring(i);
            
            ResumeLocationList.getItems().add(location);
          }

        } catch (IOException ex) {
          System.out.println(ex);
        }
      }
    });
  }

}