package com.hallvardlaerum.libs.ui;

public class UIKyklop {
    private static UIKyklop uiKyklop;



    public void vent(Integer millisekunder){
        try {
            Thread.sleep(millisekunder);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static UIKyklop hent(){
        if (uiKyklop == null) {
            uiKyklop = new UIKyklop();
        }
        return uiKyklop;
    }

    private UIKyklop() {
    }

}
