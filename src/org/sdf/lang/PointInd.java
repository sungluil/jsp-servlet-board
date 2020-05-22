package org.sdf.lang;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.sdf.log.Log;

import java.util.HashMap;

public class PointInd extends Point {
    public int pointSize = -1;

    public Evaluate eval;

    public PointInd() {
        this(new JSONObject());
    }

    public PointInd(JSONObject o) {
        initPoint(o);
        initGoal(o);
        init(o);
    }

    public PointInd(String s) {
        JSONObject o = (JSONObject) JSONValue.parse(s);
        initPoint(o);
        initGoal(o);
        init(o);
    }

    /**
     * 조회결과를 이용하여 Point Object 생성
     * @param prefix
     * @param rs
     */
    public PointInd(String prefix, IData rs) {
        try {
            // set goal
            goal = new Goal();
            goal.goal = rs.getDouble(prefix + "_point_goal");
            goal.base = rs.getDouble(prefix + "_point_base");
            goal.min = rs.getDouble(prefix + "_point_min");

            // set weight
            weight = rs.getDouble(prefix + "_point_weight");

            // set point
            String str = rs.get(prefix + "_point");
            // 등급별 점수 설정
            JSONObject o = (JSONObject) JSONValue.parse(str);
            initPoint(o);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initPoint(JSONObject o) {
        // grade 설정
        grades = Point.getGrades();

        // set points
        JSONArray jarr = o.getArray("point");
        gradeMap = new HashMap<>();
        if (jarr != null) {
            for (int i = 0; i < grades.length; i++) {
                if (i < jarr.size()) {
                    String grade = grades[i];
                    gradeMap.put(grade, getDouble(jarr.get(i)));
                }
            }
        } else {
            for (int i = 0; i < grades.length; i++) {
                String grade = grades[i];
                gradeMap.put(grade, 0.0);
            }
        }

        // set direct
        direct = o.getInt("direct");

        // set none
        none = o.getInt("none") == 1;
        none_grade = o.getString("none_grade");
        none_score = o.getDouble("none_score");

        // set convert
        cvrt = new Convert(o.getObject("convert"));

        eval = new Evaluate(o.getObject("eval"));

    }

    /**
     * 등급별 평가 점수
     *
     * @param v
     * @return
     */
    public double convertEvalValue(double v) {
        if (eval == null || eval.used != 1)
            return v;

        int g = getGradeIndex(v);
        double d = convertEvalValue(g);

        return d;

    }

    /**
     * 등급별 평가 점수
     *
     * @param g
     * @return
     */
    public double convertEvalValue(int g) {
        if (g == -1 || eval.eval.length <= g) return -1;

        return eval.eval[g];
    }


    public String permit(double v) {

        if (v < 0)
            return "";
        if (v >= goal.goal)
            return "1";
        if (v >= goal.min)
            return "2";
        return "3";
    }

    public boolean warn(double v) {
        if (v < 0)
            return false;
        // Log.bat.info( direct + ":" + v + ":"+ goal.base);
        if (direct == 1) {
            if (v > goal.base)
                return true;
            else
                return false;
        }

        if (v < goal.base)
            return true;
        else
            return false;
    }

    public boolean violate(double v) {
        if (v < 0)
            return false;
        // Log.bat.info( direct + ":" + v + ":"+ goal.min);
        if (direct == 1) {
            if (v > goal.min)
                return true;
            else
                return false;
        }

        if (v < goal.min)
            return true;
        else
            return false;
    }

    public Goal getGoal() {
        if (goal == null) {
            return new Goal();
        }
        return goal;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("-> Goal:").append(getGoal());
        return buf.toString();
    }

    public String getString() {

        StringBuffer buf = new StringBuffer();
        buf.append(this.goal.goal + "/" + this.goal.base + "/" + this.goal.min
                + " : " + this.weight);

        if (this.cvrt != null) {
            buf.append(" cvrt : " + this.cvrt.used + " : ");
            buf.append(this.cvrt.toString());
        }

        if (this.eval != null) {
            buf.append(" eval : " + this.eval.used + " : ");
            for (int i = 0; i < eval.eval.length; i++) {
                buf.append(d(eval.eval[i])).append("/");
            }
        }

        return buf.toString();
    }

    public String allString() {

        return toString();
    }

    public String toString(Goal[] g) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < g.length; i++) {
            buf.append((i + 1) + ":" + g[i]);
        }
        return buf.toString();
    }


    /**
     * 평가 점수
     */
    public class Evaluate {

        public double eval[];
        public int used = 0;

        public Evaluate() {
            eval = new double[0];
        }

        public Evaluate(JSONObject o) {
            try {
                if (eval != null) eval = null;

                if (o == null) {
                    eval = new double[0];
                    used = 0;
                } else {
                    JSONArray jarr = o.getArray("evals");

                    if (jarr != null) {
                        if (jarr.size() > 0) {
                            eval = new double[jarr.size()];
                            for (int i = 0; i < jarr.size(); i++) {
                                eval[i] = getDouble(jarr.get(i));
                            }
                        } else {
                            eval = new double[0];
                        }
                    } else {
                        eval = new double[0];
                    }

                    used = o.getInt("used");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.bat.info(e.getLocalizedMessage());
            }
        }
    }

    public static final String DEFAULT_STRING = "{\n    \"weight\":1,\n    \"s\":95,\n    \"a\":90,\n    \"b\":85,\n    \"c\":80,\n    \"goals\":{\n        \"year\":{\"goal\":95, \"min\":90},\n        \"half\":{\n    \"h1\":{\"goal\":95, \"min\":90},\n    \"h2\":{\"goal\":95, \"min\":90}\n  },\n        \"quater\":{\n    \"q1\":{\"goal\":95, \"min\":90},\"q2\":{\"goal\":95, \"min\":90},\n    \"q3\":{\"goal\":95, \"min\":90},\"q4\":{\"goal\":95, \"min\":90}\n  },\n        \"month\":{\n            \"m1\":{\"goal\":95, \"min\":90},\"m2\":{\"goal\":95, \"min\":90},\"m3\":{\"goal\":95, \"min\":90},\"m4\":{\"goal\":95, \"min\":90},\n            \"m5\":{\"goal\":95, \"min\":90},\"m6\":{\"goal\":95, \"min\":90},\"m7\":{\"goal\":95, \"min\":90},\"m8\":{\"goal\":95, \"min\":90},\n            \"m9\":{\"goal\":95, \"min\":90},\"m10\":{\"goal\":95, \"min\":90},\"m11\":{\"goal\":95, \"min\":90},\"m12\":{\"goal\":95, \"min\":90}\n        }\n    }\n}";

    public static void main(String args[]) {
        String val = "{\n" +
                "    \"point\":[95,90,85,80],\n" +
                "    \"grade\"[\"S\", \"A\", \"B\", \"C\", \"D\"],\n" +
                "    \"converts\"\n" +
                "    \"direct\":0,\n" +
                "    \"none\":0,\n" +
                "    \"convert\":{ \n" +
                "    \t\"converts\" : [100.0, 95.0, 90.0, 85.0, 80.0],\n" +
                "    \t\"used\":0\n" +
                "    },\n" +
                "    \"eval\":{ \n" +
                "    \t\"evals\" : [100.0, 95.0, 90.0, 85.0, 80.0],\n" +
                "    \t\"used\":0\n" +
                "    }\n" +
                "}";


        PointInd point = new PointInd(val);
    }
}
