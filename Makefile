

proto: src/main/proto/addressbook.proto
	protoc -I=./src/main/proto --java_out=src/main/java src/main/proto/addressbook.proto
	ls -l src/main/java/com/bonfonte/proto/addressbook
	
	
# cleanjava:
#	rmie src/generated/java/com/bonfonte/protos/addressbook
