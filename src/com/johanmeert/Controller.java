package com.johanmeert;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.security.SecureRandom;

public class Controller {

    public Label wwoord;
    public Label entropy;
    public ChoiceBox<Integer> keuze;
    public ChoiceBox<String> wwkeuze;

    public enum Opbouw {
        LETTERS("abcdefghijkmnopqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ"),
        DIGITS("1234567890"),
        SPECIAL("&#$-+*%!?@<>°(){}"),
        UPPERC("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
        LOWERC("abcdefghijklmnopqrstuvwxyz");

        public final String content;

        Opbouw(String content) {
            this.content = content;
        }
    }

    public enum Mogelijkheden {
        LETTERS("Letters"),
        LETTERS_CIJFERS("Letters en Cijfers"),
        LETTERS_CIJFERS_SPECIALS("Letters, Cijfers en specials");

        public final String content;

        Mogelijkheden(String content) {
            this.content = content;
        }
    }

    public void wmaken() {
        String sterkte = "";
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        Password password = new Password();
        if (Mogelijkheden.LETTERS.content.equals(wwkeuze.getValue())) {
            password.generatePassword(Mogelijkheden.LETTERS, keuze.getValue());
        } else if (Mogelijkheden.LETTERS_CIJFERS.content.equals(wwkeuze.getValue())) {
            password.generatePassword(Mogelijkheden.LETTERS_CIJFERS, keuze.getValue());
        } else {
            password.generatePassword(Mogelijkheden.LETTERS_CIJFERS_SPECIALS, keuze.getValue());
        }
        wwoord.setText(password.password);
        content.putString(password.password);
        clipboard.setContent(content);
        if (password.entropy < 40) sterkte = "zwak";
        else if (password.entropy < 60) sterkte = "redelijk";
        else if (password.entropy < 120) sterkte = "goed";
        else if (password.entropy < 150) sterkte = "excellent";
        else sterkte = "near perfect";
        entropy.setText("Paswoord heeft " + password.entropy + " bits entropy (" + sterkte + ")");
    }

    public class Password {

        public int entropy = 0;
        public String password;

        public void generatePassword(Mogelijkheden mogelijkheden, int lengte) {

            String wwText = switch (mogelijkheden) {
                case LETTERS -> Opbouw.LETTERS.content;
                case LETTERS_CIJFERS -> Opbouw.LETTERS.content + Opbouw.DIGITS.content;
                case LETTERS_CIJFERS_SPECIALS -> Opbouw.LETTERS.content + Opbouw.DIGITS.content + Opbouw.SPECIAL.content;
            };
            entropy = lengte * (int) (Math.log(wwText.length()) / Math.log(2));
            SecureRandom random = new SecureRandom();
            boolean stop = false;
            while (!stop) {
                password = "";
                // Generate password
                for (int teller = 0; teller < lengte; teller++) {
                    password += wwText.charAt(random.nextInt(wwText.length() - 1));
                }
                // Check password contents
                stop = switch (mogelijkheden) {
                    case LETTERS -> true; // password is ok
                    case LETTERS_CIJFERS -> password.matches(".*[" + Opbouw.UPPERC.content + "].*") && password.matches(".*[" + Opbouw.LOWERC.content + "].*") && password.matches(".*[" + Opbouw.DIGITS.content + "].*"); // password must contain letters and digits
                    case LETTERS_CIJFERS_SPECIALS -> password.matches(".*[" + Opbouw.UPPERC.content + "].*") && password.matches(".*[" + Opbouw.LOWERC.content + "].*") && password.matches(".*[" + Opbouw.DIGITS.content + "].*") && password.matches(".*[" + Opbouw.SPECIAL.content + "].*"); // password must contain letters, digits and specials
                };
            }
        }
    }

    public void initialize() {
        keuze.getItems().addAll(8, 10, 12, 15, 20, 25, 30, 35, 40, 50);
        keuze.setValue(15);
        wwkeuze.getItems().addAll(Mogelijkheden.LETTERS.content, Mogelijkheden.LETTERS_CIJFERS.content, Mogelijkheden.LETTERS_CIJFERS_SPECIALS.content);
        wwkeuze.setValue(Mogelijkheden.LETTERS_CIJFERS.content);
    }
}