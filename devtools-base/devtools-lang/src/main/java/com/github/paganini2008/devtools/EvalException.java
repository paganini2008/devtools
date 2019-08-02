package com.github.paganini2008.devtools;

import static com.github.paganini2008.devtools.io.IOUtils.NEWLINE;

import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class EvalException extends Exception {

	private static final long serialVersionUID = -5899242001009791984L;

	public EvalException(String msg, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
		super(msg);
		this.diagnostics = diagnostics;
	}

	private final List<Diagnostic<? extends JavaFileObject>> diagnostics;

	public String toString() {
		StringBuilder str = new StringBuilder();
		if (diagnostics != null) {
			for (Diagnostic<? extends JavaFileObject> d : diagnostics) {
				str.append("Code: ").append(d.getCode());
				str.append(", Kind: ").append(d.getKind());
				str.append(", Position: ").append(d.getPosition());
				str.append(", StartPosition: ").append(d.getStartPosition());
				str.append(", EndPosition: ").append(d.getEndPosition());
				str.append(", Source: ").append(d.getSource());
				str.append(", Message: ").append(d.getMessage(null));
				str.append(NEWLINE);
			}
		}
		return super.toString() + str;
	}

}
