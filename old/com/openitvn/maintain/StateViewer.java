/*
 * Copyright (C) 2017 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openitvn.maintain;

import com.openitvn.control.UCKeyboard;
import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;

/**
 *
 * @author Thinh Pham
 */
public class StateViewer extends TimerTask implements KeyEventDispatcher {
    
    private Toolkit tk;
    private JLabel lblMem, lblNum, lblCap;
    private Timer timer;
    
    public void setMemoryViewer(JLabel lblMem, JLabel lblNum, JLabel lblCap) {
        this.lblMem = lblMem;
        this.lblNum = lblNum;
        this.lblCap = lblCap;
        this.tk = Toolkit.getDefaultToolkit();
    }
    
    @Override
    public void run() {
        if (lblMem != null) {
            long total = Runtime.getRuntime().totalMemory();
            long free = Runtime.getRuntime().freeMemory();
            float used = (total - free) / 1048576f;
            lblMem.setText(String.format("Memory: %1$.2f MB", used));
        }
    }
    
    public void start() {
        timer = new Timer("MemViewer");
        timer.schedule(this, 0, 5000);
        UCKeyboard.addKeyEventDispatcher(this);
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        UCKeyboard.removeKeyEventDispatcher(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_NUM_LOCK:
                    if (lblNum != null)
                        lblNum.setEnabled(tk.getLockingKeyState(KeyEvent.VK_NUM_LOCK));
                    return true;

                case KeyEvent.VK_CAPS_LOCK:
                    if (lblCap != null)
                        lblCap.setEnabled(tk.getLockingKeyState(KeyEvent.VK_CAPS_LOCK));
                    return true;
            }
        }
        return false;
    }
}
