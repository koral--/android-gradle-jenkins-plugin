package pl.droidsonroids.gradle.jenkins

class PersistentMap implements Map<String, Boolean> {
	private final File file
	private final Properties mProperties

	PersistentMap(File file) {
		this.file = file
		file.parentFile.mkdirs()
		file.createNewFile()
		mProperties = new Properties()
		Properties properties = mProperties
		file.withReader("UTF-8") {
			properties.load(it)
		}
	}

	@Override
	int size() {
		return mProperties.size()
	}

	@Override
	boolean isEmpty() {
		return mProperties.isEmpty()
	}

	@Override
	boolean containsKey(Object key) {
		return mProperties.containsKey(key)
	}

	@Override
	boolean containsValue(Object value) {
		return mProperties.containsValue(value)
	}

	@Override
	Boolean get(Object key) {
		Object rawValue = mProperties.get(key)
		if (rawValue != null)
			Boolean.valueOf(rawValue.toString())
		else
			null
	}

	@Override
	Boolean put(String key, Boolean value) {
		def oldValue = mProperties.put(key, value.toString())
		writeToFile()
		return oldValue
	}

	@Override
	Boolean remove(Object key) {
		def value = mProperties.remove(key)
		writeToFile()
		return value
	}

	@Override
	void putAll(Map<? extends String, ? extends Boolean> m) {
		mProperties.putAll(m)
		writeToFile()
	}

	@Override
	void clear() {
		mProperties.clear()
		writeToFile()
	}

	@Override
	Set<String> keySet() {
		def strings = new HashSet<String>(mProperties.keySet().size())
		mProperties.keySet().each { strings.add(it.toString()) }
		return strings
	}

	@Override
	Collection<Boolean> values() {
		def values = new ArrayList<Boolean>(mProperties.values().size())
		mProperties.values().each { values.add(Boolean.valueOf(it.toString())) }
		return values
	}

	@Override
	Set<Map.Entry<String, Boolean>> entrySet() {
		def entries = mProperties.entrySet()
		def tempMap = new HashMap<String, Boolean>(entries.size())
		entries.each { tempMap.put(it.key.toString(), Boolean.valueOf(it.value.toString())) }
		return tempMap.entrySet()
	}

	private void writeToFile() {
		file.parentFile.mkdirs()
		file.createNewFile()
		Properties properties = mProperties
		file.withWriter("UTF-8") {
			properties.store(it, null)
		}
	}
}