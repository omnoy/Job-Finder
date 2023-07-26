package resumeBuilder.command;

public class HTMLResume {
	private String html;
	
	public HTMLResume() {
	}
	
	public HTMLResume(String html) {
		this.setHtml(html);;
	}
	
	public String getHtml() {
		return this.html;
	}
	
	public void setHtml(String html) {
		this.html = html.replace("\t", "").replace("\n", "");
	}
}