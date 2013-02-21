package kr.go.KNPA.Romeo.Menu;

public class MenuListItem {
	// �ܼ� �ؽ�Ʈ�� �ƴ� �پ��� ������ ���� �ֱ� ���Ͽ� MenuListAdapter�� ���� Object�� �����Ѵ�.

	private MenuListItem(Builder builder) {
		section = builder.section;
		title = builder.title;
		iconImage = builder.iconImage;
		code = builder.code;
	}
	
	public String toString() {
		
		return super.toString() + "\n"+"section : "+section+", title : "+title+", iconImage : "+iconImage+", code : "+code;
	}
	
	public String section;													// ���� 
	public String title;													// �̸�
	public int iconImage;													// ������
	public String code;
	public MenuListItem(String _section, String _title, int _iconImg) {		// ������
		this.section = _section;
		this.title = _title; 
		this.iconImage = _iconImg;
	}
	
	
	public static class Builder {
		private String section;
		private String title;
		private int iconImage;
		private String code;
		
		public Builder section(String _section) {
			this.section = _section;
			return this;
		}
		public Builder title(String _title) {
			this.title = _title;
			return this;
		}
		public Builder iconImage(int _iconImage) {
			this.iconImage = _iconImage;
			return this;
		}
		public Builder code(String _code) {
			this.code = _code.toUpperCase();
			return this;
		}
		
		public MenuListItem build() {
			return new MenuListItem(this);
		}
	}
}
