package org.sdf.util;

import java.util.*;

public class Calculator {

	public synchronized static String calculate(String exp) {
		String postfix = setExpression(exp);
		float val1, val2;
		Float result;
		String value;
		StringTokenizer st = new StringTokenizer(postfix, "|");
		Stack stk = new Stack();
		while (st.hasMoreTokens()) {
			value = st.nextToken();
			if (isoper(value.charAt(0))) {
				if (!stk.empty())
					val2 = ((Float) stk.pop()).floatValue(); // �ΰ�; �о����
				else
					return "Invalid Operation!"; // ����7� �޼��� ����
				if (!stk.empty())
					val1 = ((Float) stk.pop()).floatValue();
				else
					return "Invalid Operation!"; // ����7� �޼��� ����
				switch (value.charAt(0)) // ������ ���� ����; ��
				{
				case '*':
					val1 *= val2;
					break;
				case '+':
					val1 += val2;
					break;
				case '-':
					val1 -= val2;
					break;
				case '/':
					if (val2 == 0)
						return "Divide zero";
					else
						val1 /= val2;
					break;
				default:
					return "Unknown operator";
				}
				stk.push(new Float(val1)); // ����; ������ �� ��; �ٽ� stack��
											// push��
			} else {
				stk.push(new Float(value)); // �����ڰ� �ƴѰ�� stack�� push��
			}
		}
		result = (Float) stk.pop(); // ��~��� ���ÿ��� ������ return��
		if (!stk.empty())
			return "Invalid Operation!"; // �7��� ��� �7�޼��� ����
		return result.toString();
	}

	static String setExpression(String in) {
		int count = 0;
		char ch, tmp;
		boolean flag = false; // ���ڰ� �����ؼ� ���� ��츦 �Ǵ�; '�� ���
		Stack stk = new Stack();
		StringBuffer temp = new StringBuffer();
		StringBuffer out = new StringBuffer();

		while (count < in.length()) {
			ch = in.charAt(count); // infix���� �� ���� �о����
			if (isoper(ch)) // �������� ���
			{
				flag = false; // �����ڰ� ���8� ��=���ڴ� ���ڰ� ���ӵǴ� ���� �ƴϹǷ�
								// flag�� false�� �Ѵ�
				if (ch == '(')
					stk.push(new Character(ch)); // '('�� ���8� push
				else if (ch == ')') {
					while (!stk.empty()
							&& (ch = ((Character) stk.pop()).charValue()) != '(') {
						out.append('|'); // ������ ���̿� ������ �־���
						out.append(ch);
						temp.append(ch); // applet�� Label�� ����� ���ڿ�����
											// ������ ���� ��=
					}
				} else {
					while (!stk.empty()
							&& order(ch) <= order(((Character) stk.peek())
									.charValue())) {
						tmp = ((Character) stk.pop()).charValue();
						out.append('|');
						out.append(tmp);
						temp.append(tmp);
					}
					stk.push(new Character(ch));
				}
			} else if (ch == ' ') {

			} else {
				if (flag)
					out.append(ch); // �о���� ���� ������ ��쿡�� flag�� ���
									// �����ڸ� �־���
				else {
					out.append('|');
					out.append(ch);
					flag = true;
				}
				temp.append(ch);
			}
			count++;
		}
		while (!stk.empty()) {
			out.append('|');
			tmp = ((Character) stk.pop()).charValue();
			out.append(tmp);
			temp.append(tmp);
		}
		return out.toString();
		// return temp.toString();
	}

	static boolean isoper(char x) // �����ڸ� true�� ��ȯ
	{
		switch (x) {
		case '(':
		case ')':
		case '+':
		case '-':
		case '*':
		case '/':
			return true;
		default:
			return false;
		}
	}

	static int order(char x) // �켱��'�� ���ڷ� �����Ͽ� ��ȯ
	{
		switch (x) {
		case '(':
		case ')':
			return 1;
		case '+':
		case '-':
			return 2;
		case '*':
		case '/':
			return 3;
		default:
			return 0;
		}
	}

	public static void main(String[] args) {
		// cal.setStatement("(10 5 -9) *15 / 5");
		System.out.println(Calculator.calculate("(10 -8) *15 / 5"));
		System.out.println(Calculator.calculate("(10 -9) *15 / 5"));
		System.out.println(Calculator.calculate("10.002"));

	}
}