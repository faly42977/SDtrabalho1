package sys.storage;

import java.util.List;

import api.storage.BlobStorage;
import api.storage.Datanode;
import api.storage.Namenode;
import sys.storage.io.BufferedBlobReader;
import sys.storage.io.BufferedBlobWriter;

public class LocalBlobStorage implements BlobStorage {
	private static final int BLOCK_SIZE=512;

	Namenode namenode;
	DataNodeManager datanodeManager;

	public LocalBlobStorage() {
		//System.out.println("localblob.const");
		this.namenode = new NamenodeClient();
		//System.out.println("NamenodeClient ok");
		this.datanodeManager = new DataNodeManager();
		//System.out.println("DataNodeManager ok");
	}

	@Override
	public List<String> listBlobs(String prefix) {
		//System.out.println("localblob.listblobs");
		//System.out.println("searching for prefix:" + prefix);
		return namenode.list(prefix.trim());
	}

	@Override
	public void deleteBlobs(String prefix) {
		//System.out.println("localblob.deteleteblobs");
		namenode.list( prefix ).forEach( blob -> {
			namenode.read( blob ).forEach( block -> {
				datanodeManager.deleteBlock(block);
			});
		});
		namenode.delete(prefix);
	}

	@Override
	public BlobReader readBlob(String name) {
		//System.out.println("localBlob.readBlob");
		return new BufferedBlobReader( name, namenode, datanodeManager);
	}

	@Override
	public BlobWriter blobWriter(String name) {
		//System.out.println("localBlob.blobWriter");
		return new BufferedBlobWriter( name, namenode, datanodeManager, BLOCK_SIZE);
	}
}
