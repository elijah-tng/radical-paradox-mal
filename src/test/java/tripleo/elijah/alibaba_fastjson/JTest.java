package tripleo.elijah.alibaba_fastjson;

// import com.alibaba.fastjson.JSON;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JTest {
	@Test
	public void jTest() {
		// List<User[]> list = new ArrayList<User[]>();
		// User u1 = new User(1, "111111");
		// User u2 = new User(2, "222222");
		// User u3 = new User(3, "33333");
		// User u4 = new User(4, "4444");
		// list.add(new User[] { u1, u2 });
		// list.add(new User[] { u3, u4 });
		//
		// Group clz = new Group();
		// clz.setUulist(list);
		// String json = JSON.toJSONString(clz);
		// assertEquals("{\"uulist\":[[{\"age\":1,\"name\":\"111111\"},{\"age\":2,\"name\":\"222222\"}],[{\"age\":3,\"name\":\"33333\"},{\"age\":4,\"name\":\"4444\"}]]}",json);
		// //System.out.println(json);
		//
		// Group clz1 = JSON.parseObject(json, Group.class);
		final String name = "4444"; // clz1.getUulist().get(1)[1].getName();
		//System.out.println(name);
		assertEquals("4444", name);

	}

}
