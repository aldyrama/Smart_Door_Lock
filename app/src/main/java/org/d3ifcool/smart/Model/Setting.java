package org.d3ifcool.smart.Model;

public class Setting {

    String txtSetting;

    boolean isCheck;

    public Setting(String txtSetting, boolean isCheck) {

        this.txtSetting = txtSetting;

        this.isCheck = isCheck;

    }

    public String getTxtSetting() {

        return txtSetting;

    }

    public void setTxtSetting(String txtSetting) {

        this.txtSetting = txtSetting;

    }

    public boolean isCheck() {

        return isCheck;

    }

    public void setCheck(boolean check) {

        isCheck = check;

    }

}
