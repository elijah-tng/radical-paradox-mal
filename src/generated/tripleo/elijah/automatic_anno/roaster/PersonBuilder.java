package tripleo.elijah.automatic_anno.roaster;
public class PersonBuilder {

	private final Person object = new Person();

	public Person build() {
		return object;
	}

	public PersonBuilder setName(java.lang.String value) {
		this.object.setName(value);
		return this;
	}

	public PersonBuilder setAge(int value) {
		this.object.setAge(value);
		return this;
	}
}