package pl.droidsonroids.gradle.jenkins

class PersistentBooleanMap implements Map<String, Boolean> {
	private final File file
	private final Properties mStorage

	PersistentBooleanMap(File file) {
		this.file = file
		file.parentFile.mkdirs()
		file.createNewFile()
		mStorage = new Properties()
		Properties properties = mStorage
		file.withReader("UTF-8") {
			properties.load(it)
		}
	}

	@Override
	int size() {
		return mStorage.size()
	}

	@Override
	boolean isEmpty() {
		return mStorage.isEmpty()
	}

	@Override
	boolean containsKey(Object key) {
		return mStorage.containsKey(key)
	}

	@Override
	boolean containsValue(Object value) {
		return mStorage.containsValue(value)
	}

	@Override
	Boolean get(Object key) {
		Object rawValue = mStorage.get(key)
		if (rawValue != null)
			Boolean.valueOf(rawValue.toString())
		else
			null
	}

	@Override
	Boolean put(String key, Boolean value) {
		def oldValue = mStorage.put(key, value.toString())
		writeToFile()
		return oldValue
	}

	@Override
	Boolean remove(Object key) {
		def value = mStorage.remove(key)
		writeToFile()
		return value
	}

	@Override
	void putAll(Map<? extends String, ? extends Boolean> m) {
		mStorage.putAll(m)
		writeToFile()
	}

	@Override
	void clear() {
		mStorage.clear()
		writeToFile()
	}

	@Override
	Set<String> keySet() {
		def strings = new HashSet<String>(mStorage.keySet().size())
		mStorage.keySet().each { strings.add(it.toString()) }
		return strings
	}

	@Override
	Collection<Boolean> values() {
		def values = new ArrayList<Boolean>(mStorage.values().size())
		mStorage.values().each { values.add(Boolean.valueOf(it.toString())) }
		return values
	}

	@Override
	Set<Map.Entry<String, Boolean>> entrySet() {
		def entries = mStorage.entrySet()
		def tempMap = new HashMap<String, Boolean>(entries.size())
		entries.each { tempMap.put(it.key.toString(), Boolean.valueOf(it.value.toString())) }
		return tempMap.entrySet()
	}

	private void writeToFile() {
		file.parentFile.mkdirs()
		file.createNewFile()
		Properties properties = mStorage
		file.withWriter("UTF-8") {
			properties.store(it, null)
		}
	}
}