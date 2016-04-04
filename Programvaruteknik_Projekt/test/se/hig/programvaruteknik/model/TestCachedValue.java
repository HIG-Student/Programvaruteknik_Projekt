package se.hig.programvaruteknik.model;

import org.junit.Test;
import static org.junit.Assert.*;

import se.hig.programvaruteknik.model.CachedValue.CachedValueException;

@SuppressWarnings("javadoc")
public class TestCachedValue
{
    @Test(expected = CachedValueException.class)
    public void testExceptionOnNoData()
    {
	CachedValue<String> testObject = new CachedValue<>();
	testObject.get();
    }

    @Test
    public void testHaveCacheCache()
    {
	CachedValue<String> testObject = new CachedValue<>();
	assertFalse(testObject.haveValue());
	testObject.set("Yo");
	assertTrue(testObject.haveValue());
    }

    @Test
    public void testNormalCache()
    {
	CachedValue<String> testObject = new CachedValue<>();
	testObject.set("Yo");
	assertEquals("Yo", testObject.get());
    }

    @Test
    public void testClearCacheOnEmpty()
    {
	CachedValue<String> testObject = new CachedValue<>();
	testObject.clearCache();
	assertFalse(testObject.haveValue());
    }

    @Test
    public void testClearCache()
    {
	CachedValue<String> testObject = new CachedValue<>();
	testObject.set("Yo");
	testObject.clearCache();
	assertFalse(testObject.haveValue());
    }

    @Test
    public void testDoubleSet()
    {
	CachedValue<String> testObject = new CachedValue<>();
	testObject.set("Yo1");
	testObject.set("Yo2");
	assertEquals("Yo2", testObject.get());
    }

    @Test
    public void testHaveSupplier()
    {
	CachedValue<String> testObject = new CachedValue<>();
	assertFalse(testObject.haveSupplier());
	testObject.updateSupplier(() -> "Yo");
	assertTrue(testObject.haveSupplier());
    }

    @Test
    public void testCanGiveValue()
    {
	CachedValue<String> testObject = new CachedValue<>();
	assertFalse(testObject.canGiveValue());
	testObject.updateSupplier(() -> "Yo");
	assertTrue(testObject.canGiveValue());
	testObject.set("Yolo");
	assertTrue(testObject.canGiveValue());
	testObject.updateSupplier(null);
	assertFalse(testObject.canGiveValue());
	testObject.setActive(false);
	assertFalse(testObject.canGiveValue());
	testObject.updateSupplier(() -> "Yo");
	assertTrue(testObject.canGiveValue());
	testObject.updateSupplier(null);
	testObject.set(null);
	assertFalse(testObject.canGiveValue());
    }

    @Test
    public void testWithSupplier()
    {
	CachedValue<String> testObject = new CachedValue<>(() -> "Yo");
	assertEquals("Yo", testObject.get());
    }

    @Test
    public void testUpdateSupplier()
    {
	CachedValue<String> testObject = new CachedValue<>();
	testObject.set("Yo");
	testObject.updateSupplier(() ->
	{
	    return "ok";
	});
	assertEquals("ok", testObject.get());
    }

    class TestValue
    {
	public String value;
    }
}
