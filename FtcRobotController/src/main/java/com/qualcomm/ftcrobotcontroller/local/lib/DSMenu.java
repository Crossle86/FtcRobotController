/**
 * Menu utility. Displays a menu of choices on driver station with selection up/down with the
 * D pad and A button as accept choice and B button as cancel. The getChoice method loops until
 * either A or B is pressed.
 * When called from linearOpMode, use getChoice. When called from opmode, use getChoiceLoop in the
 * init_loop method and set up the menu in the init method.
 * Adapted from code contributed by team 492.
 */
package com.qualcomm.ftcrobotcontroller.local.lib;

import java.util.ArrayList;

public class DSMenu
{
    private static final long LOOP_INTERVAL = 10;

    private DSDashboard         dashboard;
    private String              menuTitle;
    private MenuButtons         menuButtons;
    private ArrayList<String>   choiceTextTable = new ArrayList<>();
    private ArrayList<Object>   choiceValueTable = new ArrayList<>();
    private int                 selectedChoice = -1;
    private boolean             firstTime = true;
    private boolean             upButtonPressed, downButtonPressed;

    // This interface is implemented by the calling opmode and so the gamepad is read in the
    // opmode class and accessible here through the interface to the opmode instance. This is
    // kind of complicated, as one could just pass in the gamepad object in the getChoiceXXX
    // methods. You can't store a reference to a gamepad in a constructor due to a bug in the SDK,
    // so you have to pass the gamepad each time you loop but that is simpler than adding the
    // interface implementation to any calling opmodes. That said, the interface is left in to
    // demonstrate how to use an interface.

    public interface MenuButtons
    {
        boolean isMenuUp();
        boolean isMenuDown();
        boolean isMenuOk();
        boolean isMenuCancel();
    }   //interface MenuButtons

    /**
     * Construct new DSMenu instance.
     * @param menuTitle Title of the menu.
     * @param menuButtons Reference to calling opmode (this).
     */
    public DSMenu(String menuTitle, MenuButtons menuButtons)
    {
        Util.log(menuTitle);

        if (menuButtons == null || menuTitle == null)
        {
            throw new NullPointerException("menuTitle/menuButtons must be provided");
        }

        dashboard = DSDashboard.getInstance();
        this.menuTitle = menuTitle;
        this.menuButtons = menuButtons;
    }   //DSMenu

    /**
     * Add a choice to the menu.
     * @param choiceText Label of the menu choice.
     * @param choiceValue Data object returned when a choice is selected. Not visible on the DS.
     */
    public void addChoice(String choiceText, Object choiceValue)
    {
        Util.log("text=%s, value=%s", choiceText, choiceValue.toString());

        choiceTextTable.add(choiceText);
        choiceValueTable.add(choiceValue);

        if (selectedChoice == -1)
        {
            selectedChoice = 0;
        }
    }   //addChoice

    private int getTheChoice()
    {
        Util.log();

        if (menuButtons.isMenuUp())
            upButtonPressed = true;
        else if (upButtonPressed)
        {
            prevChoice();
            upButtonPressed = false;
        }

        if (menuButtons.isMenuDown())
            downButtonPressed = true;
        else if (downButtonPressed)
        {
            nextChoice();
            downButtonPressed = false;
        }

        displayMenu();

        return selectedChoice;
    }   //getChoice

    /**
     * Displays the menu on DS and operates the d-pad up/down and A (accept) or B (cancel) buttons.
     * This method does not return until A or B is pressed. This method is only for LinearOpModes.
     * @return The user selection (0-based, as in first choice is returned as 0) or -1 if cancel.
     */
    public int getChoice()
    {
        Util.log();

        while (true)
        {
            if (menuButtons.isMenuCancel())
            {
                selectedChoice = -1;
                break;
            }
            else if (menuButtons.isMenuOk())
                break;

            selectedChoice = getTheChoice();

            try
            {
                Thread.sleep(LOOP_INTERVAL);
            }
            catch (InterruptedException e)
            {
            }
        }

        Util.log("choice=%d", selectedChoice);

        return selectedChoice;
    }   //getChoice

    /**
     * Displays the menu on DS and operates the d-pad up/down and A (accept) or B (cancel) buttons.
     * This method is used in regular opmodes in the init_loop method. You loop in the return value
     * which is false until the user makes a selection. To get the selection, you call getSelectedChoice.
     * @return True when the user has pressed A or B.
     */
    public boolean getChoiceLoop()
    {
        Util.log();

        if (menuButtons.isMenuCancel())
        {
            selectedChoice = -1;
            return true;
        }
        else if (menuButtons.isMenuOk())
            return true;

        selectedChoice = getTheChoice();

        Util.log("choice=%d", this.selectedChoice);

        return false;
    }   //getChoiceLoop

    /**
     * Return the menu choice selected by the user.
     * @return The user selection (0-based, as in first choice is returned as 0) or -1 if cancel.
     */
    public int getSelectedChoice() {return selectedChoice;}

    /**
     * For a choice selection, return the Object associated with that choice.
     * @param choice Choice selection (0-number of choices).
     * @return The Object instance associated with the choice.
     */
    public Object getChoiceValue(int choice)
    {
        Util.log("%d", choice);

        try
        {
            return choiceValueTable.get(choice);
        } catch (Exception e){return null;}

    }   //getChoiceValue

    private void displayMenu()
    {
        Util.log();

        int firstDisplayedChoice = 0;

        int lastDisplayedChoice =
                Math.min(firstDisplayedChoice + DSDashboard.MAX_NUM_TEXTLINES - 2,
                         choiceTextTable.size() - 1);

        if (firstTime)
        {
            dashboard.clearDisplay();
            firstTime = false;
        }

        dashboard.displayPrintf(0, menuTitle);

        for (int i = firstDisplayedChoice; i <= lastDisplayedChoice; i++)
        {
            dashboard.displayPrintf(
                    i - firstDisplayedChoice + 1,
                    i == selectedChoice ? ">>%s": "%s",
                    choiceTextTable.get(i));
        }
    }   //displayMenu

    private void nextChoice()
    {
        Util.log();

        if (choiceTextTable.size() == 0)
        {
            selectedChoice = -1;
        }
        else
        {
            selectedChoice++;

            if (selectedChoice >= choiceTextTable.size())
            {
                selectedChoice = 0;
            }
        }
    }   //nextChoice

    private void prevChoice()
    {
        Util.log();

        if (choiceTextTable.size() == 0)
        {
            selectedChoice = -1;
        }
        else
        {
            selectedChoice--;

            if (selectedChoice < 0)
            {
                selectedChoice = choiceTextTable.size() - 1;
            }
        }
    }

}   //class DSMenu
