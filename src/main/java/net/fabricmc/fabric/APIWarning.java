package net.fabricmc.fabric;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

public class APIWarning {

    public static void main(String[] args) {

        String st="Fabric API is not meant to be run.\nPlease place this file in your mods folder.";
  JOptionPane.showMessageDialog(null,st);

}
}