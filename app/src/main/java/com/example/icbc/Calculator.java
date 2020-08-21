package com.example.icbc;

import java.util.ArrayList;
import java.util.HashMap;

public class Calculator {
    // 存款利率表：央行基准利率 工行挂牌利率 存款FTP
    private HashMap<String, ArrayList<Double>> depositTable;
    // 贷款利率表：基准利率（对应的LPR利率）贷款FTP
    private HashMap<String, ArrayList<Double>> loanTable;

    public Calculator() {
        initDeposiTable();
        initLoanTable();
    }

    /**
     * 初始化存款利率表
     */
    private void initDeposiTable() {
        depositTable = new HashMap<>();

        depositTable.put("活期", new ArrayList<Double>() {{
            add(0.0035); // 央行基准利率 0.35%
            add(0.0030); // 工行挂牌利率 0.30%
            add(0.0228); // 集中价格（存款FTP）2.28%
        }});

        depositTable.put("七天", new ArrayList<Double>() {{
            add(0.0135);
            add(0.0110);
            add(0.0243);
        }});
        depositTable.put("三个月", new ArrayList<Double>() {{
            add(0.0110);
            add(0.0135);
            add(0.0295);
        }});
        depositTable.put("六个月", new ArrayList<Double>() {{
            add(0.0130);
            add(0.0155);
            add(0.0300);
        }});
        depositTable.put("一年", new ArrayList<Double>() {{
            add(0.0150);
            add(0.0175);
            add(0.0305);
        }});
        depositTable.put("两年", new ArrayList<Double>() {{
            add(0.0210);
            add(0.0225);
            add(0.0315);
        }});
        depositTable.put("三年", new ArrayList<Double>() {{
            add(0.0275);
            add(0.0275);
            add(0.0365);
        }});
        depositTable.put("五年", new ArrayList<Double>() {{
            add(0.0275);
            add(0.0275);
            add(0.0330);
        }});
    }

    /**
     * 初始化贷款利率表
     */
    private void initLoanTable() {
        final double LPR1Y = 0.0385;
        final double LPR5Y = 0.0465;
        loanTable = new HashMap<>();

        // 6个月以内(含6个月)
        loanTable.put("六个月", new ArrayList<Double>() {{
            add(LPR1Y); // 一年期LPR利率
            add(0.0290); // FTP 2.90%
        }});
        // 6个月-1年(含1年)
        loanTable.put("一年", new ArrayList<Double>() {{
            add(LPR1Y);
            add(0.0300);
        }});
        // 1年-3年(含3年)
        loanTable.put("三年", new ArrayList<Double>() {{
            add(LPR1Y);
            add(0.0305);
        }});
        // 3年-5年(含5年)
        loanTable.put("五年", new ArrayList<Double>() {{
            add(LPR1Y);
            add(0.0320);
        }});
        // 5年以上
        loanTable.put("五年以上", new ArrayList<Double>() {{
            add(LPR5Y); // 五年期LPR利率
            add(0.0325);
        }});
    }

    public double[] calculateDepositFTPIncome(double amount, boolean isCurrent, double duration, int rateType, String value) {
        double income;
        double FTPSpread;
        ArrayList<Double> depositRate = getDepositRate(isCurrent, duration);

        if (depositRate == null)
            return new double[]{0.0, 0.0};

        // 1. 央行基准利率 2.工行挂牌利率
        switch (rateType) {
            case 1:
                // 浮动比例
                double rate = Calculator.format(value);
                // 存款FTP利差:存款FTP–央行基准利率*（1+浮动比例）
                FTPSpread = depositRate.get(2) - depositRate.get(0) * (1 + rate);
                break;
            case 2:
                // 加减点
                int point = Integer.parseInt(value);
                // 存款FTP利差:存款FTP–（工行挂牌利率+加减点/10000*100%）
                FTPSpread = depositRate.get(2) - (depositRate.get(1) + point / 10000.0);
                break;
            default:
                return new double[]{0.0, 0.0};
        }

        System.out.println("存款FTP利差为：" + FTPSpread);
        //贷款FTP利差收入
        income = amount * FTPSpread * duration;

        return new double[]{FTPSpread, income};
    }

    public ArrayList<Double> getDepositRate(boolean isCurrent, double duration) {
        ArrayList<Double> rate = null;

        if (isCurrent) {
            rate = this.depositTable.get("活期");
        } else {
            if (duration == (7 / 360.0))
                // TODO:存款期限为七天的分母是360天还是365天
                rate = depositTable.get("七天");
            else if (duration == 0.25)
                rate = depositTable.get("三个月");
            else if (duration == 0.5)
                rate = depositTable.get("六个月");
            else if (duration == 1)
                rate = depositTable.get("一年");
            else if (duration == 2)
                rate = depositTable.get("两年");
            else if (duration == 3)
                rate = depositTable.get("三年");
            else if (duration == 5)
                rate = depositTable.get("五年");
            else
                System.out.println("无效输入");
        }

        return rate;
    }

    public double[] calculateLoanFTPIncome(double amount, double duration, int point) {
        double FTPSpread;
        double income;
        ArrayList<Double> loanRate = getLoanRate(duration);

        if (loanRate == null)
            return new double[]{0.0, 0.0};

        // 贷款FTP利差
        FTPSpread = loanRate.get(0) + point / 10000.0 - loanRate.get(1);
        //贷款FTP利差收入
        income = amount * FTPSpread * duration;

        return new double[]{FTPSpread, income};
    }

    public ArrayList<Double> getLoanRate(double duration) {
        ArrayList<Double> rate = null;

        if (duration > 0 && duration <= 0.5)
            rate = loanTable.get("六个月");
        else if (duration > 0.5 && duration <= 1)
            rate = loanTable.get("一年");
        else if (duration > 1 && duration <= 3)
            rate = loanTable.get("三年");
        else if (duration > 3 && duration <= 5)
            rate = loanTable.get("五年");
        else if (duration > 5)
            rate = loanTable.get("五年以上");

        return rate;
    }


    /**
     * 统一输入格式
     *
     * @param input
     * @return
     */
    public static Double format(String input) {
        if (input.contains("%")) {
            input = input.replace("%", "");
            return Double.parseDouble(input) / 100;
        }

        return Double.parseDouble(input) / 100;
    }
}
