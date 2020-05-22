package org.sdf.lang;

import com.steg.efc.GlobalConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.sdf.log.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Point {
    // 가중치
    public double weight;
    // 목표점수
    public Goal goal;

    // 등급모록
    public String grades[];    // Alias

    // 등급별 기준점수
	public Map<String, Double> gradeMap;

    // 지표점수 방향[상향/하양]
    public int direct = 0;

    public boolean none = false;

    public String none_grade = "S";

    public double none_score = 0;

    // 등급별 변환점수
    public Convert cvrt;

    public Point() {
        this(new JSONObject());
    }

    public Point(JSONObject o) {
        initPoint(o);
        initGoal(o);
        init(o);
    }

    public Point(String s) {
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
    public Point(String prefix, IData rs) {
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


    /**
     * 기본정보 초기화
     * @param o
     */
    public void init(JSONObject o) {
        weight = o.getInt("weight");
    }

    /**
     * point 정보 초기화
     * @param o
     */
    public void initPoint(JSONObject o) {
        // grade 설정
        grades = Point.getGrades();

        gradeMap = new HashMap<String, Double>();

        Iterator<String> keys = o.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            o.getDouble(key);
            gradeMap.put(key, o.getDouble(key));
        }

        direct = o.getInt("direct");

        none = o.getInt("none") == 1;
        none_grade = o.getString("none_grade");
        none_score = o.getDouble("none_score");

        // 변환점수 생성
        try {
            cvrt = new Convert(o.getObject("convert"));
        } catch (Exception e) {
            e.printStackTrace();
            cvrt = new Convert();
        }
    }

    /**
     * goal 정보 초기화
     * @param o
     */
    public void initGoal(JSONObject o) {
        goal = new Goal();
        JSONObject g = o.getObject("goals");
        // JSONObject ygoal = g.getObject("year");
        // goal.goal = ygoal.getDouble("goal");
        // goal.min = ygoal.getDouble("min");
        if (o.get("p_goal") != null) {
            goal.goal = Double.valueOf((String) o.get("p_goal"));
        } else {
            goal.goal = 0.0;
        }

        if (o.get("p_min") != null) {
            goal.min = Double.valueOf((String) o.get("p_min"));
        } else {
            goal.min = 0.0;
        }
    }

    public double getGradeValue(String grade) {
        if (gradeMap != null) {
            if (gradeMap.get(grade.toLowerCase()) != null) {
                return gradeMap.get(grade.toLowerCase());
            } else if (gradeMap.get(grade.toUpperCase()) != null) {
                return gradeMap.get(grade.toUpperCase());
            }
        }

        return 0.0;
    }

    /**
     * 점수를 이용하여 지표 등급을 반환한다.
     * @param value
     * @return
     */
    public String getGrade(double value) {
        // SLM 환경설정 정보 조회
        String[] grades = Point.getGrades();

        if (0 <= value) {
            for (int i = 0; i < grades.length; i++) {
                String grade = grades[i];
                // 마지막 등급인지 확인한다.
                if ((i + 1) < grades.length) {
                    // 마지막 등급이 아닌경우
                    double gradeValue = this.getGradeValue(grade);
                    // 지표방향 0: 하향식(높은점수가 등급이 높음), 1: 상향식(낮은점수가 등급이 높음)
                    if (this.direct == 1) {
                        // 상향식 체크
                        if ( value <= gradeValue) {
                            return grade;
                        }
                    } else {
                        // 하향식 체크
                        if ( value >= gradeValue) {
                            return grade;
                        }
                    }
                } else {
                    // 마지막 등급인 경우 등급을 리턴한다.
                    return grade;
                }
            }
        }

        return "";
    }

    /**
     * 배열형식으로 저장되어 있는 등급 정보를 배열 인덱스 정보를 이용하여 등급정보를 반환한다.
     * @param index
     * @return
     */
    public String getGrade(int index) {
        if (grades.length > index) {
            return grades[index];
        }

        return "";
    }

    /**
     * 동일한 Alias 가 존재 할 경우 대비 Index로 돌려 줌.
     *
     * @param v
     * @return
     */
    public int getGradeIndex(double v) {
        if (v == -1) {
            return -1;

        } else {
            // grade 정보
            String grade = this.getGrade(v);
            return getGradeIndex(grade);
        }
    }

    /**
     * 등급정보룰 이용하여 등급 index를 반환한다.
     * @param grade
     * @return
     */
    public int getGradeIndex(String grade) {
        for (int i = 0; i < grades.length; i++) {
            if (grade.equals(grades[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 등급점수로 환산점수를 구한다.
     * @param v
     * @return
     */
    public double convertValue(double v) {
        // 환산점수 정보 확인, 정보가 없으면 점수를 그대로 반환한다.
        if (this.cvrt == null || this.cvrt.used != 1)
            return v;
        // 점수를 이용하여 등급정보를 구한다.
        String g = this.getGrade(v);
        // 등급정보를 이용하여 환산점수를 구한다.
        double d = this.getGradeConvertValue(g);
        // 환산점수 반환
        return d;
    }

    /**
     * 해당지표의 등급으로 환산점수를 가져온다.
     * @param g
     * @return
     */
    public double getGradeConvertValue(String g) {
        int index = getGradeIndex(g);
        return cvrt.value(index);
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

    public String permitString(double v) {
        String s = "";
        if (v < 0)
            return "blank";
        s = (v < 0 ? "" : String.valueOf((int) v / 10));
        if (v >= goal.goal)
            return "p_1_" + s;
        if (v >= goal.min)
            return "p_2_" + s;
        return "p_3_" + s;
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
        buf.append("[Point]" + gradeMap.toString());
        buf.append("-> Goal:").append(getGoal());
        return buf.toString();
    }

    public String getString() {

        StringBuffer buf = new StringBuffer();
        buf.append(this.goal.goal + "/" + this.goal.base + "/" + this.goal.min
                + " : " + this.weight);
        buf.append(" : " + this.direct + " : " + this.none + " : ");

        Iterator<String> keys =  gradeMap.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            buf.append("/" + gradeMap.get(key));
        }

        if (this.cvrt != null) {
            buf.append(" : " + this.cvrt.used + " : ");
            buf.append(this.cvrt.toString());

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
     * convert double to String
     * @param d
     * @return
     */
    public String d(double d) {
        return String.valueOf(d);
    }

    /**
     * convert Object to double
     *
     * @param o
     * @return
     */
    public double getDouble(Object o) {
        try {
            if (o instanceof String) {
                return Double.parseDouble((String) o);
            }

            Number n = (Number) o;
            return n.doubleValue();
        } catch (Exception e) {
            Log.act.info("Point Case Error original=" + o.toString() + " \n" + e.getLocalizedMessage());
            return 0;
        }
    }

    /**
     * 목표점수 관리용
     */
    public class Goal {
        public double goal;
        public double min;
        public double base;

        protected Goal() {
            this(0,0, 0);
        }

        protected Goal(double goal, double min) {
            this(goal, 0, min);
        }

        protected Goal(double goal, double base, double min) {
            this.goal   = goal;
            this.min    = min;
            this.base   = base;
        }

        public String toString() {
            return d(goal) + ":" + d(min) + ":" + d(base);
        }
    }

    /**
     * 등급별 환산점수를 가지고 있고, 등급에 해당되는 환산점수를 리턴한다.
     */
    public class Convert {

        // 환산점수 사용 여부, 1: 사용, 0: 사용안함
        public int used = 0;

        // 환산점수
        public double converts[];

        /**
         * 기본 생성자 사용시 빈 객체를 생성한다.
         */
        public Convert() {
            this(null);
        }

        /**
         * 등급별 환산점수를 JSONObject로 받아서 처리하도록 한다.
         * 환산점수는 {S: 100, A: 800}과 같이 key&value 형식으로 저장되어 있어야 한다.
         * @param o
         */
        public Convert(JSONObject o) {
            if (o != null) {
                // 배열 형태로 저장되어 있는 환산점수를 설정
                JSONArray arr = o.getArray("converts");

                if (arr != null) {
                    converts = new double[grades.length];
                    for (int i = 0; i < grades.length; i++) {
                        if (i < arr.size()) {
                            converts[i] = getDouble(arr.get(i));
                        } else {
                            converts[i] = getDouble(0);
                        }
                    }
                }

                // 사용여부
                used = o.getInt("used");
            }
        }

        /**
         * 등급 환산점수를 반환한다.
         * @param index
         * @return
         */
        public double value(int index) {
            if (converts != null && index != -1 && index < converts.length) {
                return converts[index];
            }
            return 0.0;
        }

        public String toString() {
            return converts.toString();
        }
    }

    /**
     * GlobalConfig에 설정된 등급정보를 리턴한다. 설정된 값이 없다면 기본 등급 목록을 리턴한다. 기본 등급 목록은 [S,A,B,C,D]
     * @return 등급목록
     */
    public static String[] getGrades() {
        // grade 설정
        try{
            String cfgStr = GlobalConfig.getInstance().getValue("slm");
            JSONObject slmConfig = (JSONObject)JSONValue.parse(cfgStr);
            if (slmConfig != null) {
                return slmConfig.getArrayString("grades");
            }
        } catch (Exception ex) {
            Log.biz.err("slm 설정 정보가 없거나, 잘못된 정보가 구성되어 있습니다.");
        }

        return new String[]{"S","A","B","C","D"};

    }


    /**
     * GlobalConfig에 설정된 기준 가중치 점수를 리턴한다.
     * @return
     */
    public static double getBaseWeight() {
        try {
            String cfgStr = GlobalConfig.getInstance().getValue("slm");
            JSONObject slmConfig = (JSONObject)JSONValue.parse(cfgStr);
            if (slmConfig != null) {
                return slmConfig.getDouble("baseweight");
            }
        } catch (Exception ex) {
            Log.biz.err("slm 설정 정보가 없거나, 잘못된 정보가 구성되어 있습니다.");
        }
        return 100.0;
    }

    /**
     * 가중치 평가환산점수 기준점수
     * @return
     */
    public static double getWeightPoint() {
        try {
            String cfgStr = GlobalConfig.getInstance().getValue("slm");
            JSONObject slmConfig = (JSONObject)JSONValue.parse(cfgStr);
            if (slmConfig != null) {
                return slmConfig.getDouble("weightpoint");
            }
        } catch (Exception ex) {
            Log.biz.err("slm 설정 정보가 없거나, 잘못된 정보가 구성되어 있습니다.");
        }
        return 100.0;
    }
    /**
     * GlobalConfig에 설정된 기준점수를 리턴한다. 설정된 정보가 없으면 100점을 리턴한다.
     * @return
     */
    public static double getBasePoint() {
        try {
            String cfgStr = GlobalConfig.getInstance().getValue("slm");
            JSONObject slmConfig = (JSONObject)JSONValue.parse(cfgStr);
            if (slmConfig != null) {
                return slmConfig.getDouble("basepoint");
            }
        } catch (Exception ex) {
            Log.biz.err("slm 설정 정보가 없거나, 잘못된 정보가 구성되어 있습니다.");
        }
        return 100.0;
    }

    public static final String DEFAULT_STRING = "{\n    \"weight\":1,\n    \"s\":95,\n    \"a\":90,\n    \"b\":85,\n    \"c\":80,\n    \"goals\":{\n        \"year\":{\"goal\":95, \"min\":90},\n        \"half\":{\n    \"h1\":{\"goal\":95, \"min\":90},\n    \"h2\":{\"goal\":95, \"min\":90}\n  },\n        \"quater\":{\n    \"q1\":{\"goal\":95, \"min\":90},\"q2\":{\"goal\":95, \"min\":90},\n    \"q3\":{\"goal\":95, \"min\":90},\"q4\":{\"goal\":95, \"min\":90}\n  },\n        \"month\":{\n            \"m1\":{\"goal\":95, \"min\":90},\"m2\":{\"goal\":95, \"min\":90},\"m3\":{\"goal\":95, \"min\":90},\"m4\":{\"goal\":95, \"min\":90},\n            \"m5\":{\"goal\":95, \"min\":90},\"m6\":{\"goal\":95, \"min\":90},\"m7\":{\"goal\":95, \"min\":90},\"m8\":{\"goal\":95, \"min\":90},\n            \"m9\":{\"goal\":95, \"min\":90},\"m10\":{\"goal\":95, \"min\":90},\"m11\":{\"goal\":95, \"min\":90},\"m12\":{\"goal\":95, \"min\":90}\n        }\n    }\n}";
}
